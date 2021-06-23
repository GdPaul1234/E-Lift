package main.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.Dialog;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextField;
import main.model.Adresse;
import main.model.Immeuble;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ImmeubleEditDialogController {

    @FXML
    private TextField nomTextField;
    @FXML
    private Spinner<Integer> etageSpinner;
    @FXML
    private TextField rueTextField;
    @FXML
    private TextField codePostalTextField;
    @FXML
    private TextField villeTextField;

    @FXML
    private void initialize() {
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialog
     */
    public void setDialog(Dialog<Immeuble> dialog) {

        dialog.setResultConverter(
                buttonType -> {
                    if (Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                        // TODO get longitude et get latitude
                        return new Immeuble(nomTextField.getText(), etageSpinner.getValue(), new Adresse(rueTextField.getText(), villeTextField.getText(), codePostalTextField.getText(), 0, 0));
                    }
                    return null;
                }
        );
    }

    /**
     * Sets the immeuble to be edited in the dialog.
     *
     * @param immeuble
     */
    public void setImmeuble(@NotNull Immeuble immeuble) {
        nomTextField.setText(immeuble.getNom());
        etageSpinner.getValueFactory().setValue(immeuble.getNbEtage());
        rueTextField.setText(immeuble.getAdresse().getRue());
        codePostalTextField.setText(immeuble.getAdresse().getCodePostal());
        villeTextField.setText(immeuble.getAdresse().getVille());
    }
}
