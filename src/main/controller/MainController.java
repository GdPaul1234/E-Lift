package main.controller;

import com.sothawo.mapjfx.*;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import javafx.util.Pair;
import main.controller.DAO.*;
import main.model.*;
import main.model.enums.EtatAscenseur;
import main.model.interfaces.Ressource;
import main.view.AscensoristeOverview;
import main.view.GestionnaireOverview;
import main.view.ImmeubleOverview;
import main.view.custom.RessourceTreeCell;
import main.view.dialog.ImmeubleEditDialog;
import main.view.dialog.PersonneEditDialog;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.*;
import java.util.stream.Collectors;

public class MainController {
    private final List<Marker> positionImmeubles = Collections.synchronizedList(new ArrayList<>());
    private final ObjectProperty<TreeItem<Ressource>> rootItem = new SimpleObjectProperty<>(new TreeItem<>(new Personne()));
    @FXML
    private HBox topbar;
    @FXML
    private MapView mapView;
    @FXML
    private ComboBox<EtatAscenseur> filtrePanneComboBox;
    @FXML
    private TreeView<Ressource> ascenseurTreeView;

    public static void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(e.getMessage());
        alert.showAndWait();

        // TODO deleted this
        e.printStackTrace();
    }

    @FXML
    private void initialize() {
        long start = System.currentTimeMillis();

        // Masquer les actions non permises selon les roles
        // https://stackoverflow.com/a/30019190
        Set<Node> removeItems = new HashSet<>();

        if (DataAccess.isAscensoriste()) {
            removeItems.addAll(topbar.lookupAll(".gestionnaire-control"));
            System.out.println("Bienvenue ascensoriste");
        }

        if (DataAccess.isGestionnaire()) {
            removeItems.addAll(topbar.lookupAll(".ascensoriste-control"));
            System.out.println("Bienvenue gestionnaire");
        }

        topbar.getChildren().removeAll(removeItems);

        initComboBox();

        // init treeview params
        ascenseurTreeView.setShowRoot(false);
        ascenseurTreeView.rootProperty().bind(rootItem);
        ascenseurTreeView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
        ascenseurTreeView.setCellFactory(ressourceTreeView -> new RessourceTreeCell<>());
        ascenseurTreeView.getSelectionModel().selectedItemProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue != null) {
                Ressource ressource = newValue.getValue();
                if (ressource instanceof Immeuble) {
                    Adresse adresse = ((Immeuble) ressource).getAdresse();
                    mapView.setZoom(16);
                    mapView.setCenter(new Coordinate(Float.valueOf(adresse.getLatitude()).doubleValue(), Float.valueOf(adresse.getLongitude()).doubleValue()));
                }
            }
        });

        updateTreeView();
        initMapViewTask();

        // attendre la fin de l'initialisation de la TreeView et de la MapView pour afficher les marqueurs
        mapView.initializedProperty().addListener((observableValue, oldValue, newValue) -> {
            if (newValue) showUpdatedMarker();
        });

        System.out.printf("Start MainPage : %d ms\n", System.currentTimeMillis() - start);
    }

    private void initComboBox() {
        // Remplir la Combobox des etats d'un ascenseur
        ObservableList<EtatAscenseur> etats = FXCollections.observableArrayList(EtatAscenseur.getValues());
        filtrePanneComboBox.setItems(etats);
        filtrePanneComboBox.getSelectionModel().selectedItemProperty()
                .addListener((observableValue, oldValue, newValue) -> updateTreeView());
    }

    private void initMapViewTask() {
        // Init MapView to center of France
        Runnable task = () -> {
            mapView.setCenter(new Coordinate(46.603354, 1.8883335));
            mapView.setZoom(5);
            mapView.setAnimationDuration(750);
            mapView.initialize();
        };
        task.run();
    }


    @FXML
    private void handleUpdate() {
        updateTreeView();
    }

    @FXML
    private void handleClearFilter() {
        filtrePanneComboBox.setValue(null);
    }

    /* ********************************************* *
     *                Gestion carte                  *
     * ********************************************* */
    private void updateTreeView() {
        positionImmeubles.forEach(mapView::removeMarker);
        positionImmeubles.clear();

        final Service<Void> updateListAscenseur = new Service<>() {
            @Override
            protected Task<Void> createTask() {
                return new Task<>() {

                    @Override
                    protected Void call() throws Exception {
                        // Get Filter
                        EtatAscenseur etatAscenseur = filtrePanneComboBox.getValue();

                        ImmeubleDAO immeubleDAO = new ImmeubleDAO();
                        AscenseurDAO ascenseurDAO = new AscenseurDAO();

                        TreeItem<Ressource> rootItem = new TreeItem<>(new Personne());
                        rootItem.getChildren().clear();
                        rootItem.setExpanded(true);

                        for (Immeuble immeuble : DataAccess.isGestionnaire() ? immeubleDAO.getMyImmeubles() : immeubleDAO.getAllImmeubles()) {
                            ImageView immeubleIcon = new ImageView(new Image(getClass().getResourceAsStream("/main/resource/imeuble 16.png")));
                            TreeItem<Ressource> immeubleNode = new TreeItem<>(immeuble, immeubleIcon);
                            immeubleNode.setExpanded(true);

                            int nbPanne = 0, nbPannePersonne = 0, nbReparation = 0;

                            for (Ascenseur ascenseur : ascenseurDAO.getAscenseursImmeuble(immeuble.getIdImmeuble())) {
                                switch (ascenseur.getState()) {
                                    case EnPanne -> nbPanne++;
                                    case EnPannePersonnes -> nbPannePersonne++;
                                    case EnCoursDeReparation -> nbReparation++;
                                }

                                // filtre ascenseur par etatAscenseur
                                if (etatAscenseur == null || ascenseur.getState() == etatAscenseur) {
                                    TreeItem<Ressource> ascenseurLeaf = new TreeItem<>(ascenseur);
                                    immeubleNode.getChildren().add(ascenseurLeaf);
                                }

                            }

                            // ajouter noeud immeuble si pas vide
                            if ((nbPanne > 0 && etatAscenseur == EtatAscenseur.EnPanne) ||
                                    (nbPannePersonne > 0 && etatAscenseur == EtatAscenseur.EnPannePersonnes) ||
                                    (nbReparation > 0 && etatAscenseur == EtatAscenseur.EnCoursDeReparation) ||
                                    etatAscenseur == EtatAscenseur.EnService ||
                                    etatAscenseur == null
                            ) {
                                rootItem.getChildren().add(immeubleNode);

                                // Afficher marqueur immeuble sur la carte
                                Adresse adresse = immeuble.getAdresse();
                                Marker.Provided markerColor = (nbPanne > 0 || nbPannePersonne > 0) ? Marker.Provided.RED :
                                        ((nbReparation > 0) ? Marker.Provided.ORANGE : Marker.Provided.GREEN);
                                Marker marker = Marker.createProvided(markerColor)
                                        .setPosition(new Coordinate(Float.valueOf(adresse.getLatitude()).doubleValue(), Float.valueOf(adresse.getLongitude()).doubleValue()))
                                        .attachLabel(new MapLabel(immeuble.getNom()));
                                positionImmeubles.add(marker);
                            }


                        }

                        if (Platform.isFxApplicationThread()) {
                            MainController.this.rootItem.set(rootItem);
                        } else {
                            Platform.runLater(() -> MainController.this.rootItem.set(rootItem));
                        }

                        return null;
                    }
                };
            }
        };

        updateListAscenseur.start();
        updateListAscenseur.setOnSucceeded((WorkerStateEvent event) -> {
            ascenseurTreeView.refresh();
            if (mapView.getInitialized()) {
                showUpdatedMarker();
            }
        });
    }

    private synchronized void showUpdatedMarker() {
        positionImmeubles.forEach(v -> {
            v.setVisible(true);
            mapView.addMarker(v);
        });

        if (positionImmeubles.size() > 1) {
            Extent extentImmeubles = Extent.forCoordinates(positionImmeubles.stream()
                    .map(MapCoordinateElement::getPosition).collect(Collectors.toList()));
            mapView.setExtent(extentImmeubles);
        } else if (positionImmeubles.size() == 1) {
            mapView.setCenter(positionImmeubles.get(0).getPosition());
        }

    }


    /* ********************************************* *
     *                Gestion Personne               *
     * ********************************************* */
    private <T extends Personne> void handleAddPersonne(T role) {
        PersonneEditDialog dialog = new PersonneEditDialog(null);
        dialog.setTitle(role.getClass().getSimpleName());
        Pair<Personne, String> userInput = dialog.showPersonDialog();

        if (userInput != null) {

            // Verify is all field are not empty
            if (userInput.getKey().isValid() && !userInput.getValue().isEmpty()) {

                if (role instanceof Ascensoriste || role instanceof Gestionnaire) {
                    try {
                        String login;

                        if (role instanceof Ascensoriste) {
                            AscensoristeDAO ascensoristeDAO = new AscensoristeDAO();
                            Ascensoriste ascensoriste = new Ascensoriste(userInput.getKey());
                            login = ascensoristeDAO.getLoginBuilder(ascensoriste.getNom(), ascensoriste.getPrenom());

                            new AscensoristeDAO().addAscensoriste(ascensoriste, login, userInput.getValue());
                        } else {
                            GestionnaireDAO gestionnaireDAO = new GestionnaireDAO();
                            Gestionnaire gestionnaire = new Gestionnaire(userInput.getKey());
                            login = gestionnaireDAO.getLoginBuilder(gestionnaire.getNom(), gestionnaire.getPrenom());

                            new GestionnaireDAO().addGestionnaire(gestionnaire, login, userInput.getValue());
                        }

                        Alert alert = new Alert(Alert.AlertType.INFORMATION);
                        alert.setHeaderText("Votre identifiant pour vous connecter\nà votre espace personnel");
                        alert.setContentText(MessageFormat.format("{0}\nGardez-le précieusement !", login));
                        alert.showAndWait();
                    } catch (SQLException e) {
                        showError(e);
                    }
                } else {
                    throw new IllegalArgumentException("Unexpected value: " + role);
                }
            }

        }

    }

    @FXML
    private void handleAddAscensoriste() {
        handleAddPersonne(new Ascensoriste());
    }

    @FXML
    private void handleEditAscensoriste() throws Exception {
        new AscensoristeOverview().start(new Stage());
    }

    @FXML
    private void handleAddGestionnaire() {
        handleAddPersonne(new Gestionnaire());
    }

    @FXML
    private void handleEditGestionnaire() throws Exception {
        new GestionnaireOverview().start(new Stage());
    }

    /* ********************************************* *
     *                Gestion Immeuble               *
     * ********************************************* */
    @FXML
    void handleAddImmeuble() {

        // Ask user input
        Immeuble userInput = new ImmeubleEditDialog(null).showImmeubleDialog();

        if (userInput != null) {
            try {
                // Verify is all field are not empty
                if (userInput.isValid()) {
                    new ImmeubleDAO().addImmeuble(userInput);
                }
            } catch (SQLException e) {
                showError(e);
            }
        }
    }

    @FXML
    private void handleEditImmeuble() throws Exception {
        new ImmeubleOverview().start(new Stage());
    }
}
