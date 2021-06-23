package main.controller.dialog;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
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

    private final Immeuble immeuble = new Immeuble();
    private final Adresse adresse = new Adresse();

    @FXML
    private void initialize() {
        // Data Binding
        immeuble.nomProperty().bind(nomTextField.textProperty());
        immeuble.nbEtageProperty().bind(etageSpinner.valueProperty());
        adresse.rueProperty().bind(rueTextField.textProperty());
        adresse.codePostalProperty().bind(codePostalTextField.textProperty());
        adresse.villeProperty().bind(villeTextField.textProperty());
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialog
     */
    public void setDialog(Dialog<Immeuble> dialog) {
        // Prevent a dialog from closing until some aspect of the dialog becomes internally consistent
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Dialog.html
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {
            if (!immeuble.isValid() && !adresse.isValid()) {
                event.consume();
            } else {
                immeuble.setAdresse(adresse);
            }
        });

        // TODO get longitude et get latitude

        dialog.setResultConverter(
                buttonType -> {
                    if (Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData()))
                        return immeuble;
                    else return null;
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
