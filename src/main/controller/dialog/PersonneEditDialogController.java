package main.controller.dialog;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;
import main.model.Personne;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PersonneEditDialogController {

    @FXML
    private TextField nomTextField,prenomTextField,telephoneTextField;
    @FXML
    private PasswordField passwordTextField;

    private final Personne personne = new Personne();
    private final StringProperty password = new SimpleStringProperty();

    // https://code.makery.ch/fr/library/javafx-tutorial/part3/

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
        // Data Binding
        personne.nomProperty().bind(nomTextField.textProperty());
        personne.prenomProperty().bind(prenomTextField.textProperty());
        personne.telephoneProperty().bind(telephoneTextField.textProperty());
        password.bind(passwordTextField.textProperty());
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialog
     */
    public void setDialog(Dialog<Pair<Personne, String>> dialog) {
        // Prevent a dialog from closing until some aspect of the dialog becomes internally consistent
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Dialog.html
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (!personne.isValid()) {
                event.consume();
            }
        });

        dialog.setResultConverter(
                buttonType -> {
                    if (Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                        return new Pair<>(personne, password.get());
                    } else return null;
                }
        );
    }

    /**
     * Sets the person to be edited in the dialog.
     *
     * @param personne
     */
    public void setPersonne(@NotNull Personne personne) {

        prenomTextField.setText(personne.getPrenom());
        nomTextField.setText(personne.getNom());
        telephoneTextField.setText(personne.getTelephone());
    }

}
