package main.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import main.model.interfaces.PlanningRessource;

import java.util.Date;

public class TrajetAller implements PlanningRessource {
    private final ObjectProperty<Reparation> reparation = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> dateTrajet = new SimpleObjectProperty<>();
    private final IntegerProperty dureeTrajet = new SimpleIntegerProperty();

    public TrajetAller(Reparation reparation, Date dateTrajet, int dureeTrajet) {
        this.reparation.set(reparation);
        this.dateTrajet.set(dateTrajet);
        this.dureeTrajet.set(dureeTrajet);
    }

    public TrajetAller() {}

    public Reparation getReparation() {
        return reparation.get();
    }

    public void setReparation(Reparation reparation) {
        this.reparation.set(reparation);
    }

    public ObjectProperty<Reparation> reparationProperty() {
        return reparation;
    }

    public Date getDateTrajet() {
        return dateTrajet.get();
    }

    public void setDateTrajet(Date dateTrajet) {
        this.dateTrajet.set(dateTrajet);
    }

    public ObjectProperty<Date> dateTrajetProperty() {
        return dateTrajet;
    }

    public int getDureeTrajet() {
        return dureeTrajet.get();
    }

    public void setDureeTrajet(int dureeTrajet) {
        this.dureeTrajet.set(dureeTrajet);
    }

    public IntegerProperty dureeTrajetProperty() {
        return dureeTrajet;
    }
}
