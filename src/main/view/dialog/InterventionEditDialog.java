package main.view.dialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Pair;
import main.controller.dialog.InterventionEditDialogController;
import main.model.Intervention;

import java.io.IOException;

public class InterventionEditDialog {
    // https://code.makery.ch/fr/library/javafx-tutorial/part3/

    private Dialog<Pair<Integer, String>> dialog;
    private InterventionEditDialogController controller;

    public InterventionEditDialog(Stage stage) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/main/view/fxml/InterventionEditDialog.fxml"));
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

    public Pair<Integer, String> showInterventionDialog(Intervention intervention) {
        controller.setIntervention(intervention);
        // Show the dialog and wait until the user closes it
        return dialog.showAndWait().orElse(null);
    }
}
