package main.controller;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import main.controller.DAO.AscenseurDAO;
import main.controller.DAO.ImmeubleDAO;
import main.model.Adresse;
import main.model.Ascenseur;
import main.model.Immeuble;
import main.model.enums.EtatAscenseur;
import main.view.ImmeubleEditDialog;

import java.sql.SQLException;
import java.util.Date;
import java.util.concurrent.CompletableFuture;

public class ImmeubleOverviewController {

    // Immeuble
    @FXML
    private TextField nomTextField, etageTextField, adresseTextField;
    @FXML
    private TableView<Immeuble> immeubleTable;
    @FXML
    private TableColumn<Immeuble, String> nomColumn, villeColumn;
    private ObservableList<Immeuble> immeubles;

    // Ascenseur
    @FXML
    private TextField idAscenseurTextField, marqueTextField, modeleTextField;
    @FXML
    private TextField etatTextField, miseServiceTextField;
    @FXML
    private TextField etageAscenseurTextField;
    @FXML
    private TableView<Ascenseur> ascenseurTable;
    @FXML
    private TableColumn<Ascenseur, EtatAscenseur> etatColumn;
    @FXML
    private TableColumn<Ascenseur, String> modeleColumn;
    @FXML
    private TableColumn<Ascenseur, Date> miseServiceColumn;
    private ObservableList<Ascenseur> ascenseurs;


    @FXML
    private void initialize() throws Exception {
        // Initialisation des colonnes
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        villeColumn.setCellValueFactory(cellData -> cellData.getValue().adresseProperty().get().villeProperty());

        etatColumn.setCellValueFactory(cellData -> cellData.getValue().stateProperty());
        modeleColumn.setCellValueFactory(cellData -> {
            Ascenseur v = cellData.getValue();
            return new SimpleStringProperty(v.getMarque() + v.getModele(), "modèle");
        });
        miseServiceColumn.setCellValueFactory(cellData -> cellData.getValue().dateMiseEnServiceProperty());

        // Remplir la table avec les données récupérees
        updateImmeublesData();

        // Listen for selection changes and show the person details when changed.
        immeubleTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showImmeubleDetails(newValue));
    }

    private void updateImmeublesData() throws SQLException {
        immeubles = FXCollections.observableArrayList(new ImmeubleDAO().getAllImmeubles());
        immeubleTable.setItems(immeubles);
    }

    private void updateAscenseursData() throws SQLException {
        ascenseurs = FXCollections.observableArrayList(new AscenseurDAO().getAllAscenseurs());
        ascenseurTable.setItems(ascenseurs);
    }

    private void showImmeubleDetails(Immeuble immeuble) {
        if (immeuble != null && immeuble.getAdresse() != null) {
            nomTextField.setText(immeuble.getNom());
            etageTextField.setText(Integer.valueOf(immeuble.getNbEtage()).toString());

            Adresse adresse = immeuble.getAdresse();
            adresseTextField.setText(adresse.getRue() + ", " + adresse.getCodePostal() + " " + adresse.getVille());
        } else {
            nomTextField.setText("");
            etageTextField.setText("");
            adresseTextField.setText("");
        }
    }

    /* ********************************************* *
     *                Gestion Immeuble               *
     * ********************************************* */
    @FXML
    private void handleAddImmeuble() throws SQLException {
        CompletableFuture<Void> addImmeuble = new CompletableFuture<>();
        new MainController().handleAddImmeuble();
        updateImmeublesData();
    }

    @FXML
    private void handleEditImmeuble() {
        Immeuble selectedItem = immeubleTable.getSelectionModel().getSelectedItem();

        if(selectedItem != null) {
            boolean reaskEdit = false;
            do {
                // Ask user input
                Immeuble userInput = new ImmeubleEditDialog(null).showImmeubleEditDialog(selectedItem);

                if (userInput != null) {
                    try {
                        // Verify is all field are not empty
                        if (userInput.isValid()) {
                            new ImmeubleDAO().editImmeuble(selectedItem.getIdImmeuble(), userInput);
                            updateImmeublesData();
                            reaskEdit = false;
                        } else reaskEdit = true;
                    } catch (SQLException e) {
                        MainController.showError(e);
                    }
                } else {
                    reaskEdit = false;
                }
            } while (reaskEdit);
        }

    }

    @FXML
    private void handleDeleteImmeuble() {
        Immeuble selectedItem = immeubleTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            int idImmeuble = selectedItem.getIdImmeuble();
            try {
                new ImmeubleDAO().removeImmeuble(idImmeuble);
                updateImmeublesData();
            } catch (SQLException e) {
                MainController.showError(e);
            }

        }
    }
}
