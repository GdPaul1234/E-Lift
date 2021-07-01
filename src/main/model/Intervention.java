package main.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import main.model.interfaces.PlanningRessource;

import java.util.Date;

public class Intervention implements PlanningRessource {
    private final ObjectProperty<Reparation> reparation = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> dateIntervention = new SimpleObjectProperty<>();

    public Intervention(Reparation reparation, Date dateIntervention) {
        this.reparation.set(reparation);
        this.dateIntervention.set(dateIntervention);
    }

    public Reparation getReparation() {
        return reparation.get();
    }

    public void setReparation(Reparation reparation) {
        this.reparation.set(reparation);
    }

    public ObjectProperty<Reparation> reparationProperty() {
        return reparation;
    }

    public Date getDateIntervention() {
        return dateIntervention.get();
    }

    public void setDateIntervention(Date dateIntervention) {
        this.dateIntervention.set(dateIntervention);
    }

    public ObjectProperty<Date> dateInterventionProperty() {
        return dateIntervention;
    }
}
