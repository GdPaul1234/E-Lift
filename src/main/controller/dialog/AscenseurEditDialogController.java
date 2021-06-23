package main.controller.dialog;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.model.Ascenseur;
import main.model.enums.EtatAscenseur;
import org.jetbrains.annotations.NotNull;

import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Objects;

public class AscenseurEditDialogController {
    @FXML
    private TextField marqueTextField, modeleTextField;
    @FXML
    private ComboBox<EtatAscenseur> etatComboBox;
    @FXML
    private DatePicker miseServiceDatePicker;
    @FXML
    private Spinner<Integer> etageEnCoursSpinner;

    private final Ascenseur ascenseur = new Ascenseur();

    @FXML
    private void initialize() {
        // Remplir la Combobox des etats d'un ascenseurs
        ObservableList<EtatAscenseur> etats = FXCollections.observableArrayList(EtatAscenseur.getValues());
        etatComboBox.setItems(etats);

        // DataBinding
        ascenseur.marqueProperty().bind(marqueTextField.textProperty());
        ascenseur.modeleProperty().bind(modeleTextField.textProperty());
        ascenseur.stateProperty().bind(etatComboBox.valueProperty());
        ascenseur.etageProperty().bind(etageEnCoursSpinner.valueProperty());
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialog
     */
    public void setDialog(Dialog<Ascenseur> dialog) {
        // Prevent a dialog from closing until some aspect of the dialog becomes internally consistent
        // https://docs.oracle.com/javase/8/javafx/api/javafx/scene/control/Dialog.html
        final Button btOk = (Button) dialog.getDialogPane().lookupButton(ButtonType.OK);
        btOk.addEventFilter(ActionEvent.ACTION, event -> {

            if(miseServiceDatePicker.getValue() != null) {
                // LocalDate to Date conversion
                // https://stackoverflow.com/a/40143687
                Date miseEnService = Date.from(miseServiceDatePicker.getValue().atStartOfDay(ZoneId.of("Europe/Paris")).toInstant());
                ascenseur.setDateMiseEnService(miseEnService);
            }

            if (!ascenseur.isValid()) {
                event.consume();
            }
        });


        dialog.setResultConverter(
                buttonType -> {
                    if (Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData()))
                        return ascenseur;
                    else
                        return null;
                }
        );
    }

    /**
     * Sets the immeuble to be edited in the dialog.
     *
     * @param ascenseur
     */
    public void setAscenseur(@NotNull Ascenseur ascenseur) {
        // Date to LocalDate conversion
        // + encapsulate sql date do util date since toInstant since toInstant is not Implemented (No time part)
        // https://stackoverflow.com/a/40143687
        LocalDate ldMiseEnService = new Date(ascenseur.getDateMiseEnService().getTime()).toInstant().atZone(ZoneId.of("Europe/Paris")).toLocalDate();

        marqueTextField.setText(ascenseur.getMarque());
        modeleTextField.setText(ascenseur.getModele());
        etatComboBox.setValue(ascenseur.getState());
        miseServiceDatePicker.setValue(ldMiseEnService);
        etageEnCoursSpinner.getValueFactory().setValue(ascenseur.getEtage());
    }
}
