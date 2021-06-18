package main.controller;


import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.util.Pair;
import main.controller.DAO.AscensoristeDAO;
import main.model.Ascensoriste;
import main.model.Personne;
import main.view.PersonneEditDialog;

import java.sql.SQLException;

public class MainController {

    @FXML
    private void handleAddAscensoriste() {
        Platform.runLater(() -> {
            boolean reaskAdd = false;
            do {
                // Ask user input
                Pair<Personne,String> userInput = new PersonneEditDialog(null).showPersonDialog();

                if(userInput != null) {
                    try {
                        // Verify is all field are not empty
                        if(userInput.getKey().isValid() && !userInput.getValue().isEmpty()) {
                            new AscensoristeDAO().addAscensoriste(new Ascensoriste(userInput.getKey()), userInput.getValue());
                            reaskAdd = false;
                        } else reaskAdd = true;
                    } catch (SQLException throwables) {
                        throwables.printStackTrace();
                    }
                } else {
                    reaskAdd = false;
                }
            } while (reaskAdd);
        });
    }
}
