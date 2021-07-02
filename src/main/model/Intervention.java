package main.model;

import javafx.beans.property.*;
import main.model.interfaces.PlanningRessource;

import java.util.Date;

public class Intervention implements PlanningRessource {
    private final ObjectProperty<Reparation> reparation = new SimpleObjectProperty<>();
    private final ObjectProperty<Date> dateIntervention = new SimpleObjectProperty<>();
    private final IntegerProperty avancement = new SimpleIntegerProperty(null, "avancement");

    public Intervention(Reparation reparation, Date dateIntervention) {
        this.reparation.set(reparation);
        this.dateIntervention.set(dateIntervention);
    }

    public int getAvancement() {
        return avancement.get();
    }

    public void setAvancement(int avancement) {
        this.avancement.set(avancement);
    }

    public IntegerProperty avancementProperty() {
        return avancement;
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
