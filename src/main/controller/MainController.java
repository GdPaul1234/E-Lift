package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.controller.DAO.AscensoristeDAO;
import main.controller.DAO.GestionnaireDAO;
import main.controller.DAO.ImmeubleDAO;
import main.model.Ascensoriste;
import main.model.Gestionnaire;
import main.model.Immeuble;
import main.model.Personne;
import main.view.*;

import java.sql.SQLException;

public class MainController {

    public static void showError(Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setContentText(e.getMessage());
        alert.showAndWait();

        // TODO deleted this
        e.printStackTrace();
    }

    /* ********************************************* *
     *                Gestion Personne               *
     * ********************************************* */
    private <T extends Personne> void handleAddPersonne(T role) {
            boolean reaskAdd = false;
            do {
                // Ask user input
                Pair<Personne, String> userInput = new PersonneEditDialog(null).showPersonDialog();

                if (userInput != null) {
                    try {
                        // Verify is all field are not empty
                        if (userInput.getKey().isValid() && !userInput.getValue().isEmpty()) {
                            if (role instanceof Ascensoriste) {
                                new AscensoristeDAO().addAscensoriste(new Ascensoriste(userInput.getKey()), userInput.getValue());
                            } else if (role instanceof Gestionnaire) {
                                new GestionnaireDAO().addGestionnaire(new Gestionnaire(userInput.getKey()), userInput.getValue());
                            } else {
                                throw new IllegalArgumentException("Unexpected value: " + role);
                            }
                            reaskAdd = false;
                        } else reaskAdd = true;
                    } catch (SQLException e) {
                        showError(e);
                    }
                } else {
                    reaskAdd = false;
                }
            } while (reaskAdd);
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
            boolean reaskAdd = false;
            do {
                // Ask user input
                Immeuble userInput = new ImmeubleEditDialog(null).showImmeubleDialog();

                if (userInput != null) {
                    try {
                        // Verify is all field are not empty
                        if (userInput.isValid()) {
                            new ImmeubleDAO().addImmeuble(userInput);
                            reaskAdd = false;
                        } else reaskAdd = true;
                    } catch (SQLException e) {
                        showError(e);
                    }
                } else {
                    reaskAdd = false;
                }
            } while (reaskAdd);
    }

    @FXML private void handleEditImmeuble() throws Exception {
        new ImmeubleOverview().start(new Stage());
    }
}
