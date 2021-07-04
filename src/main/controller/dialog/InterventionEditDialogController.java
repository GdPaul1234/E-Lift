package main.controller.dialog;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.util.Pair;
import main.model.Intervention;
import main.model.Reparation;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InterventionEditDialogController {
    @FXML
    private TextField ascenseurTextField, adresseTextField;
    @FXML
    private Slider avancementSlider;
    @FXML
    private TextArea commentaireTextArea;

    private IntegerProperty avancement = new SimpleIntegerProperty();
    private StringProperty commentaire = new SimpleStringProperty();

    @FXML
    private void initialize() {
        // Data Binding
        avancement.bindBidirectional(avancementSlider.valueProperty());
        commentaire.bindBidirectional(commentaireTextArea.textProperty());
    }

    /**
     * Sets the stage of this dialog.
     *
     * @param dialog
     */
    public void setDialog(Dialog<Pair<Integer, String>> dialog) {

        dialog.setResultConverter(
                buttonType -> {
                    if (Objects.equals(ButtonBar.ButtonData.OK_DONE, buttonType.getButtonData())) {
                        return new Pair<>(avancement.getValue(), commentaire.getValue());
                    } else return null;
                }
        );
    }

    /**
     * Sets intervention to be edited in the dialog.
     *
     * @param intervention
     */
    public void setIntervention(@NotNull Intervention intervention) {
        avancement.set(intervention.getAvancement());

        Reparation reparation = intervention.getReparation();
        commentaire.set(reparation.getCommentaire());
        ascenseurTextField.setText(reparation.getAscenseur().toString());
        adresseTextField.setText(reparation.getImmeuble().getAdresse().toString());
    }

}
