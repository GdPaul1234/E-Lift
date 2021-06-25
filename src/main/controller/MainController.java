package main.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Dialog;
import javafx.scene.control.MenuButton;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.controller.DAO.AscensoristeDAO;
import main.controller.DAO.DataAccess;
import main.controller.DAO.GestionnaireDAO;
import main.controller.DAO.ImmeubleDAO;
import main.model.Ascensoriste;
import main.model.Gestionnaire;
import main.model.Immeuble;
import main.model.Personne;
import main.view.AscensoristeOverview;
import main.view.GestionnaireOverview;
import main.view.ImmeubleOverview;
import main.view.dialog.ImmeubleEditDialog;
import main.view.dialog.PersonneEditDialog;

import javax.xml.crypto.Data;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.HashSet;
import java.util.Set;

public class MainController {
    @FXML private HBox topbar;

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

        if(DataAccess.isGestionnaire()) {
            removeItems.addAll(topbar.lookupAll(".ascensoriste-control"));
            System.out.println("Bienvenue gestionnaire");
        }

        topbar.getChildren().removeAll(removeItems);
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
