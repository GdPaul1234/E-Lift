package main.view.component;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import main.model.*;
import main.model.interfaces.PlanningRessource;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class PlanningListCell extends ListCell<PlanningRessource> {
    private final Label dateLabel = new Label();
    private final Label heureLabel = new Label();
    private final Label typeEvenementPlanningLabel = new Label();
    private final Label ascenseurLabel = new Label();
    private final Label adresseLabel = new Label();

    private final HBox root = new HBox();

    public PlanningListCell() throws IOException {
        root.setSpacing(10);
        root.setPadding(new Insets(0, 0, 5, 0));

        VBox dateBox = new VBox();
        dateBox.setAlignment(Pos.CENTER);
        {
            dateBox.getChildren().add(dateLabel);
            dateLabel.setStyle("-fx-font-size: 1.1em");
            dateBox.getChildren().add(heureLabel);
        }
        root.getChildren().add(dateBox);

        VBox infoPanneBox = new VBox();
        infoPanneBox.setAlignment(Pos.CENTER_LEFT);
        {
            typeEvenementPlanningLabel.setStyle("-fx-font-size: 1.1em; -fx-font-weight: bold");
            infoPanneBox.getChildren().add(typeEvenementPlanningLabel);

            ascenseurLabel.setStyle("-fx-font-size: 1.1em");
            infoPanneBox.getChildren().add(ascenseurLabel);

            adresseLabel.setStyle("-fx-font-style: italic");
            infoPanneBox.getChildren().add(adresseLabel);
        }
        root.getChildren().add(infoPanneBox);
    }

    @Override
    protected void updateItem(PlanningRessource item, boolean empty) {
        super.updateItem(item, empty);
        setText(null);
        setGraphic(null);

        DateFormat dateFormatter = new SimpleDateFormat("dd/MM");
        DateFormat heureFormatter = new SimpleDateFormat("HH:mm");

        if (!empty && item != null) {
            typeEvenementPlanningLabel.setText(item.getClass().getSimpleName());

            if (item instanceof Reparation) {
                typeEvenementPlanningLabel.setText("Panne " + ((Reparation) item).getType());
                dateLabel.setText(dateFormatter.format(((Reparation) item).getDatePanne()));
                heureLabel.setText(heureFormatter.format(((Reparation) item).getDatePanne()));

                ascenseurLabel.setText(((Reparation) item).getAscenseur().toString());
                adresseLabel.setText(((Reparation) item).getImmeuble().getAdresse().toString());

                root.setStyle("-fx-background-color: rgba(255,0,0, 0.6);");
            } else if (item instanceof Intervention) {
                dateLabel.setText(dateFormatter.format(((Intervention) item).getDateIntervention()));
                heureLabel.setText(heureFormatter.format(((Intervention) item).getDateIntervention()));

                Reparation reparation = ((Intervention) item).getReparation();
                if (reparation != null) {
                    if (reparation.getLoginAscensoriste() != null) {
                        String loginAscensoriste = reparation.getLoginAscensoriste();
                        String extractedHumanId = String.join(" ", loginAscensoriste.split("(\\.)|(@)|(\\d)", 2));
                        typeEvenementPlanningLabel.setText("Intervention de " + extractedHumanId);
                    }

                    Ascenseur ascenseur = reparation.getAscenseur();
                    ascenseurLabel.setText(String.format("%d%% de %d minutes sur %s %s",
                            ((Intervention) item).getAvancement(), reparation.getDuree(),
                            ascenseur.getMarque(), ascenseur.getModele()));
                    adresseLabel.setText(reparation.getImmeuble().getAdresse().toString());
                }

                root.setStyle("-fx-background-color: rgba(228, 255, 127, 0.6);");
            } else if (item instanceof TrajetAller) {
                typeEvenementPlanningLabel.setText(String.format("Trajet (%d minutes)",
                        ((TrajetAller) item).getDureeTrajet()));

                dateLabel.setText(dateFormatter.format(((TrajetAller) item).getDateTrajet()));
                heureLabel.setText(heureFormatter.format(((TrajetAller) item).getDateTrajet()));

                Reparation reparation = ((TrajetAller) item).getReparation();
                if (reparation != null) {
                    Ascenseur ascenseur = reparation.getAscenseur();
                    ascenseurLabel.setText(String.format("Sur site pour réparer %s %s",
                            ascenseur.getMarque(), ascenseur.getModele()));
                    adresseLabel.setText(((TrajetAller) item).getReparation().getImmeuble().getAdresse().toString());

                }

                root.setStyle("-fx-background-color: rgba(255, 163, 63, 0.6)");
            }

            setGraphic(root);
        }
    }

}
