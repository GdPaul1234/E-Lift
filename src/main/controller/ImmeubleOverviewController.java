package main.controller;

import javafx.beans.binding.Bindings;
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
import main.view.dialog.AscenseurEditDialog;
import main.view.dialog.ImmeubleEditDialog;

import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

public class ImmeubleOverviewController {

    // Immeuble
    @FXML
    private TextField nomTextField, etageTextField, adresseTextField;
    @FXML
    private TableView<Immeuble> immeubleTable;
    @FXML
    private TableColumn<Immeuble, String> nomColumn, villeColumn;

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
    private TableColumn<Ascenseur, Integer> idColumn;
    @FXML
    private TableColumn<Ascenseur, EtatAscenseur> etatColumn;
    @FXML
    private TableColumn<Ascenseur, String> modeleColumn;
    @FXML
    private TableColumn<Ascenseur, Date> miseServiceColumn;
    private Immeuble selectedImmeuble;


    @FXML
    private void initialize() throws Exception {
        // Initialisation des colonnes
        nomColumn.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        villeColumn.setCellValueFactory(cellData -> cellData.getValue().adresseProperty().get().villeProperty());

        idColumn.setCellValueFactory(cellData -> cellData.getValue().idAscenseurProperty().asObject());
        etatColumn.setCellValueFactory(cellData -> cellData.getValue().stateProperty());
        modeleColumn.setCellValueFactory(cellData -> {
            Ascenseur v = cellData.getValue();
            // Javafx concatenation of multiple StringProperty
            // https://stackoverflow.com/questions/25325209/javafx-concatenation-of-multiple-stringproperty
            return Bindings.concat(v.marqueProperty(), ", ", v.modeleProperty());
        });
        miseServiceColumn.setCellValueFactory(cellData -> cellData.getValue().dateMiseEnServiceProperty());

        // Remplir la table avec les données récupérees
        updateImmeublesData();

        // Listen for selection changes and show the immeuble details when changed.
        immeubleTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> {
                    // keep track of selected item, even if focus is lost
                    selectedImmeuble = newValue != null ? newValue : oldValue;
                    showImmeubleDetails(newValue);
                });

        // Listen for selection changes and show the ascenseur details when changed.
        ascenseurTable.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showAscenseurDetails(newValue));

        immeubleTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        ascenseurTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
    }

    private void updateImmeublesData() throws SQLException {
        ObservableList<Immeuble> immeubles = FXCollections.observableArrayList(new ImmeubleDAO().getAllImmeubles());
        immeubleTable.setItems(immeubles);
    }

    private void updateAscenseursData() throws SQLException {
        if (selectedImmeuble != null) {
            ObservableList<Ascenseur> ascenseurs = FXCollections.observableArrayList(new AscenseurDAO().getAscenseursImmeuble(selectedImmeuble.getIdImmeuble()));
            ascenseurTable.setItems(ascenseurs);
        }
    }

    private void showImmeubleDetails(Immeuble immeuble) {
        if (immeuble != null && immeuble.getAdresse() != null) {
            nomTextField.setText(immeuble.getNom());
            etageTextField.setText(Integer.valueOf(immeuble.getNbEtage()).toString());

            Adresse adresse = immeuble.getAdresse();
            adresseTextField.setText(adresse.getRue() + ", " + adresse.getCodePostal() + " " + adresse.getVille());

            // Update sa liste d'ascenseurs
            try {
                updateAscenseursData();
            } catch (SQLException e) {
                MainController.showError(e);
            }
        } else {
            nomTextField.setText("");
            etageTextField.setText("");
            adresseTextField.setText("");
        }
    }

    private void showAscenseurDetails(Ascenseur ascenseur) {
        if (ascenseur != null) {
            // Human readable date diff
            // https://stackoverflow.com/a/22588328
            Duration d = Duration.between(new Date(ascenseur.getDateMiseEnService().getTime()).toInstant(), Instant.now());

            idAscenseurTextField.setText(Integer.valueOf(ascenseur.getIdAscenseur()).toString());
            marqueTextField.setText(ascenseur.getMarque());
            modeleTextField.setText(ascenseur.getModele());
            etatTextField.setText(ascenseur.getState().toString());
            miseServiceTextField.setText(new SimpleDateFormat("yyyy-MM-dd").format(ascenseur.getDateMiseEnService()) + " (" + d.toDays() + " j) ");
            etageAscenseurTextField.setText(Integer.valueOf(ascenseur.getEtage()).toString());
        } else {
            idAscenseurTextField.setText("");
            marqueTextField.setText("");
            modeleTextField.setText("");
            etatTextField.setText("");
            miseServiceTextField.setText("");
            etageAscenseurTextField.setText("");
        }
    }

    /* ********************************************* *
     *                Gestion Immeuble               *
     * ********************************************* */
    @FXML
    private void handleAddImmeuble() throws SQLException {
        new MainController().handleAddImmeuble();
        updateImmeublesData();
    }

    @FXML
    private void handleEditImmeuble() {
        Immeuble selectedItem = immeubleTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            // Ask user input
            Immeuble userInput = new ImmeubleEditDialog(null).showImmeubleEditDialog(selectedItem);

            if (userInput != null) {
                try {
                    // Verify is all field are not empty
                    if (userInput.isValid()) {
                        new ImmeubleDAO().editImmeuble(selectedItem.getIdImmeuble(), userInput);
                        updateImmeublesData();
                    }
                } catch (SQLException e) {
                    MainController.showError(e);
                }
            }
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

    /* ********************************************* *
     *               Gestion Ascenseur               *
     * ********************************************* */
    @FXML
    private void handleAddAscenseur() {

        if (selectedImmeuble != null) {
            int idImmeuble = selectedImmeuble.getIdImmeuble();

                // Ask user input
                Ascenseur userInput = new AscenseurEditDialog(null).showAscenseurDialog();

                if (userInput != null) {
                    try {
                        // Verify is all field are not empty
                        if (userInput.isValid()) {
                            new AscenseurDAO().addAscenseur(userInput, idImmeuble);
                            updateAscenseursData();
                        }
                    } catch (SQLException e) {
                        MainController.showError(e);
                    }
                }
        }

    }

    @FXML
    private void handleEditAscenseur() {
        Ascenseur selectedItem = ascenseurTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
                // Ask user input
                Ascenseur userInput = new AscenseurEditDialog(null).showAscenseurEditDialog(selectedItem);

                if (userInput != null) {
                    try {
                        // Verify is all field are not empty
                        if (userInput.isValid()) {
                            new AscenseurDAO().editAscenseur(selectedItem.getIdAscenseur(), userInput);
                            updateAscenseursData();
                        }
                    } catch (SQLException e) {
                        MainController.showError(e);
                    }
                }
        }
    }

    @FXML
    private void handleDeleteAscenseur() {
        Ascenseur selectedItem = ascenseurTable.getSelectionModel().getSelectedItem();

        if (selectedItem != null) {
            int idAscenseur = selectedItem.getIdAscenseur();
            try {
                new AscenseurDAO().removeAscenceur(idAscenseur);
                updateAscenseursData();
            } catch (SQLException e) {
                MainController.showError(e);
            }

        }
    }
}
