package main.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import main.controller.DAO.AscensoristeDAO;
import main.controller.DAO.GestionnaireDAO;
import main.model.Ascensoriste;
import main.model.Gestionnaire;
import main.model.Personne;
import main.view.dialog.PersonneEditDialog;

import java.sql.SQLException;

public class PersonneOverviewController<T extends Personne> {

    private final T role;
    @FXML
    private TableView<T> personneTable;
    @FXML
    private TableColumn<? extends Personne, String> nomColumn, prenomColumn;
    @FXML
    private Button editButton, delButton;
    @FXML
    private TextField nomTextField, prenomTextField, telephoneTextField, loginTextField;
    private ObservableList<? extends Personne> personnes;

    public PersonneOverviewController(T role) {
        this.role = role;
    }

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     * https://code.makery.ch/fr/library/javafx-tutorial/part2/
     */
    @FXML
    private void initialize() throws Exception {
        fetchData();

        // add button listener
        editButton.setOnAction(e -> handleEditPersonne());
        delButton.setOnAction(e -> handleDeletePersonne());

        // Intialiser les colonnes avec les property (value,name) à remplir
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        prenomColumn.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());

        // Remplir la table avec les données récupérees
        personneTable.setItems((ObservableList<T>) personnes);

        // Listen for selection changes and show the person details when changed.
        personneTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showPersonDetails(newValue));
    }

    private void showPersonDetails(T personne) {
        if (personne != null) {
            nomTextField.setText(personne.getNom());
            prenomTextField.setText(personne.getPrenom());
            telephoneTextField.setText(personne.getTelephone());
            loginTextField.setText(personne.getLogin());
        } else {
            nomTextField.setText("");
            prenomTextField.setText("");
            telephoneTextField.setText("");
            loginTextField.setText("");
        }
    }

    private void fetchData() throws SQLException {
        // Get personnes
        if (role instanceof Ascensoriste) {
            personnes = FXCollections.observableArrayList(new AscensoristeDAO().getAllAscensoristes());
        } else if (role instanceof Gestionnaire) {
            personnes = FXCollections.observableArrayList(new GestionnaireDAO().getAllGestionnaires());
        } else {
            throw new IllegalArgumentException("Unexpected value: " + role);
        }
    }

    private void updateDate() throws SQLException {
        fetchData();
        personneTable.setItems((ObservableList<T>) personnes);
    }

    private void handleDeletePersonne() {
        T selectedItem = personneTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            try {
                String login = selectedItem.getLogin();

                if (role instanceof Ascensoriste) {
                    new AscensoristeDAO().deleteAscensoriste(login);
                } else if (role instanceof Gestionnaire) {
                    new GestionnaireDAO().deleteGestionnaire(login);
                } else {
                    throw new IllegalArgumentException("Unexpected value: " + role);
                }

                updateDate();
            } catch (SQLException e) {
                MainController.showError(e);
            }

        }
    }

    private void handleEditPersonne() {
        T selectedItem = personneTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            String login = selectedItem.getLogin();

            // Ask user input
            Pair<Personne, String> userInput = new PersonneEditDialog(null).showPersonEditDialog(selectedItem);

            if (userInput != null) {
                System.out.println(login);

                try {
                    // Verify is all field are not empty
                    if (userInput.getKey().isValid()) {
                        if (role instanceof Ascensoriste) {
                            new AscensoristeDAO().editAscensoriste(login, new Ascensoriste(userInput.getKey()));
                            updateDate();
                        } else if (role instanceof Gestionnaire) {
                            new GestionnaireDAO().editGestionnaire(login, new Gestionnaire(userInput.getKey()));
                            updateDate();
                        } else {
                            throw new IllegalArgumentException("Unexpected value: " + role);
                        }
                    }
                } catch (SQLException e) {
                    MainController.showError(e);
                }
            }
        }

    }


}
