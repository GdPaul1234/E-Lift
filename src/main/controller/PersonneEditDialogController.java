package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.util.Pair;
import main.model.Personne;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class PersonneEditDialogController {

    @FXML
    private TextField nomTextField;
    @FXML
    private TextField prenomTextField;
    @FXML
    private TextField telephoneTextField;
    @FXML
    private PasswordField passwordTextField;

    // https://code.makery.ch/fr/library/javafx-tutorial/part3/

    private Dialog<Pair<Personne, String>> dialog;
    private Personne personne;

    /**
     * Initializes the controller class. This method is automatically called
     * after the fxml file has been loaded.
     */
    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialog
     */
    public void setDialog(Dialog<Pair<Personne, String>> dialog) {
        this.dialog = dialog;

        dialog.setResultConverter(
                buttonType -> {
                    if (Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                        return new Pair<>(
                                new Personne(nomTextField.getText(), prenomTextField.getText(), telephoneTextField.getText()),
                                passwordTextField.getText()
                        );
                    }
                    return null;
                }
        );
    }

    /**
     * Sets the person to be edited in the dialog.
     *
     * @param personne
     */
    public void setPersonne(@NotNull Personne personne) {
        this.personne = personne;

        prenomTextField.setText(personne.getPrenom());
        nomTextField.setText(personne.getNom());
        telephoneTextField.setText(personne.getTelephone());
    }

}
