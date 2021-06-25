package main.view.dialog;

import javafx.fxml.FXMLLoader;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import main.controller.dialog.ImmeubleEditDialogController;
import main.model.Immeuble;

import java.io.IOException;
import java.net.URISyntaxException;

public class ImmeubleEditDialog {
    // https://code.makery.ch/fr/library/javafx-tutorial/part3/

    private Dialog<Immeuble> dialog;
    private ImmeubleEditDialogController controller;

    public ImmeubleEditDialog(Stage stage) {
        try {
            // Load the fxml file and create a new stage for the popup dialog.
            FXMLLoader loader = new FXMLLoader();
            loader.setLocation(getClass().getResource("/main/view/fxml/ImmeubleEditDialog.fxml"));
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

    public Immeuble showImmeubleEditDialog(Immeuble immeuble) {
        // Set the immeuble into the controller.
        try {
            controller.setImmeuble(immeuble);
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
        return showImmeubleDialog();
    }

    public Immeuble showImmeubleDialog() {
        // Show the dialog and wait until the user closes it
        return dialog.showAndWait().orElse(null);
    }
}
