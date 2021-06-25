package main.controller.dialog;

import javafx.beans.property.ListProperty;
import javafx.beans.property.SimpleListProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import main.model.Adresse;
import main.model.Immeuble;
import main.model.apiAdresse.Feature;
import main.model.apiAdresse.FeatureCollection;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public class ImmeubleEditDialogController {

    private final Immeuble immeuble = new Immeuble();
    private final ListProperty<Feature> features = new SimpleListProperty<>();
    private final long delay = 750;
    @FXML
    private TextField nomTextField;
    @FXML
    private Spinner<Integer> etageSpinner;
    @FXML
    private ComboBox<Feature> adresseComboBox;
    @FXML
    private TextField coordTextField;
    private Adresse adresse = new Adresse();
    private long starTimeInputAdresse;

    @FXML
    private void initialize() {
        // Data Binding
        immeuble.nomProperty().bind(nomTextField.textProperty());
        immeuble.nbEtageProperty().bind(etageSpinner.valueProperty());

        TextField adresseInput = adresseComboBox.getEditor();
        adresseComboBox.itemsProperty().bind(features);

        adresseInput.setOnKeyReleased(keyEvent -> {
            AtomicLong oldTimeInputAdresse = new AtomicLong(starTimeInputAdresse);
            starTimeInputAdresse = System.currentTimeMillis();

            CompletableFuture.delayedExecutor(delay, TimeUnit.MILLISECONDS).execute(() -> {
                if (starTimeInputAdresse - oldTimeInputAdresse.get() >= delay) {

                    AdresseSuggestionWorker worker = new AdresseSuggestionWorker(adresseInput.getText());
                    oldTimeInputAdresse.set(System.currentTimeMillis());

                    CompletableFuture<ObservableList<Feature>> completableFuture = CompletableFuture.supplyAsync(() -> {
                        try {
                            return worker.call();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        return FXCollections.emptyObservableList();
                    });
                    completableFuture.thenAccept(f -> {
                        if (f.size() > 0) features.set(f);
                    });
                }
            });

        });
    }

    @FXML
    private void handleSelectedAdresse() {
        int index = adresseComboBox.getSelectionModel().getSelectedIndex();

        if (index != -1 && index < features.size()) {
            Feature value = features.get(index);
            adresse = value.toAdresse();
            coordTextField.setText(adresse.getLatitude() + ", " + adresse.getLongitude());
        }
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
            immeuble.setAdresse(adresse);
            if (!immeuble.isValid()) {
                event.consume();
            }
        });

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
    public void setImmeuble(@NotNull Immeuble immeuble) throws URISyntaxException, IOException {
        nomTextField.setText(immeuble.getNom());
        etageSpinner.getValueFactory().setValue(immeuble.getNbEtage());

        adresse = immeuble.getAdresse();
        coordTextField.setText(adresse.getLatitude() + ", " + adresse.getLongitude());

        adresseComboBox.getEditor().setText(adresse.toString());
        coordTextField.setText(adresse.getLatitude() + ", " + adresse.getLongitude());
    }

    public static class AdresseSuggestionWorker extends Task<ObservableList<Feature>> {
        private final String query;

        public AdresseSuggestionWorker(String query) {
            this.query = query;
        }

        @Override
        protected ObservableList<Feature> call() throws Exception {
            return FXCollections.observableList(new FeatureCollection(query).getFeatures());
        }
    }
}
