package main.controller;

import com.sothawo.mapjfx.Coordinate;
import com.sothawo.mapjfx.Extent;
import com.sothawo.mapjfx.MapView;
import com.sothawo.mapjfx.Marker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.controller.DAO.*;
import main.model.*;
import main.model.enums.EtatAscenseur;
import main.model.interfaces.Ressource;
import main.view.AscensoristeOverview;
import main.view.GestionnaireOverview;
import main.view.ImmeubleOverview;
import main.view.dialog.ImmeubleEditDialog;
import main.view.dialog.PersonneEditDialog;

import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.FutureTask;

public class MainController {
    @FXML
    private HBox topbar;
    @FXML
    private MapView mapView;
    @FXML
    private ComboBox<EtatAscenseur> filtrePanneComboBox;
    @FXML
    private TreeView<Ressource> ascenseurTreeView;

    private Extent extentAllLocations;
    private List<Marker> positionImmeubles;

    public static void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(e.getMessage());
        alert.showAndWait();

        // TODO deleted this
        e.printStackTrace();
    }

    @FXML
    private void initialize() {
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
        updateTreeViewTask().run();
        initMapViewTask().run();
    }

    private void initComboBox() {
        // Remplir la Combobox des etats d'un ascenseur
        ObservableList<EtatAscenseur> etats = FXCollections.observableArrayList(EtatAscenseur.getValues());
        filtrePanneComboBox.setItems(etats);
    }

    private Runnable initMapViewTask() {
        // Init MapView to center of France
        return () -> {
            mapView.setCenter(new Coordinate(46.603354, 1.8883335));
            mapView.setZoom(5);
            mapView.initialize();
        };
    }

    @FXML
    private void handleUpdate() {
        updateTreeViewTask().run();
    }

    private Runnable updateTreeViewTask() {
        final Service<TreeItem<Ressource>> updateListAscenseur = new Service<>() {

            @Override
            protected Task<TreeItem<Ressource>> createTask() {
                return new Task<>() {

                    @Override
                    protected TreeItem<Ressource> call() throws Exception {
                        ImmeubleDAO immeubleDAO = new ImmeubleDAO();
                        AscenseurDAO ascenseurDAO = new AscenseurDAO();

                        TreeItem<Ressource> rootItem = new TreeItem<>(new Personne());
                        rootItem.setExpanded(true);

                        for (Immeuble immeuble : DataAccess.isGestionnaire() ? immeubleDAO.getMyImmeubles() : immeubleDAO.getAllImmeubles()) {
                            ImageView immeubleIcon = new ImageView(new Image(getClass().getResourceAsStream("/main/resource/imeuble 16.png")));
                            TreeItem<Ressource> immeubleNode = new TreeItem<>(immeuble, immeubleIcon);
                            immeubleNode.setExpanded(true);
                            rootItem.getChildren().add(immeubleNode);

                            // TODO ajout immeuble sur carte

                            for (Ascenseur ascenseur : ascenseurDAO.getAscenseursImmeuble(immeuble.getIdImmeuble())) {
                                TreeItem<Ressource> ascenseurLeaf = new TreeItem<>(ascenseur);
                                immeubleNode.getChildren().add(ascenseurLeaf);
                            }
                        }

                        return rootItem;
                    }
                };
            }
        };

        return () -> {
            updateListAscenseur.start();
            updateListAscenseur.setOnSucceeded((WorkerStateEvent event) -> {
                ascenseurTreeView.setShowRoot(false);
                ascenseurTreeView.setRoot(updateListAscenseur.getValue());
            });
        };

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
