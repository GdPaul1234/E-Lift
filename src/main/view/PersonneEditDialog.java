package main.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.controller.PersonneEditDialogController;
import main.model.Personne;

import java.io.IOException;

public class PersonneEditDialog {
    // https://code.makery.ch/fr/library/javafx-tutorial/part3/

    private Dialog<Pair<Personne, String>> dialog;
    private PersonneEditDialogController controller;

    public PersonneEditDialog(Stage stage) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("PersonneEditDialog.fxml"));
            DialogPane page = loader.load();

            // Create the dialog Stage.
            dialog = new Dialog<>();
            dialog.initModality(Modality.WINDOW_MODAL);
            dialog.initOwner(stage);
            dialog.setDialogPane(page);

            // Get the controller
            controller = loader.getController();
            controller.setDialog(dialog);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Pair<Personne, String> showPersonEditDialog(Personne personne) {
            // Set the person into the controller.
            controller.setPersonne(personne);
            return showPersonDialog();
    }

    public Pair<Personne, String> showPersonDialog() {
        // Show the dialog and wait until the user closes it
        return dialog.showAndWait().orElse(null);
    }
}
