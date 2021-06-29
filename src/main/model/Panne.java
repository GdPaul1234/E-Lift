package main.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import main.model.enums.TypeReparation;

public class Panne {
    private ObjectProperty<Immeuble> immeuble = new SimpleObjectProperty<>(null, "Immeuble");
    private ObjectProperty<Ascenseur> ascenseur = new SimpleObjectProperty<>(null, "Ascenseur");
    private ObjectProperty<TypeReparation> typeReparation = new SimpleObjectProperty<>(null, "Type");
    private IntegerProperty avancement = new SimpleIntegerProperty(0, "avancement");
    private ObjectProperty<Ascensoriste> ascensoriste = new SimpleObjectProperty<>(null,"Ascensoriste");
    private ObjectProperty<Ascensoriste> trajetAller = new SimpleObjectProperty<>(null,"Ascensoriste");

    public Panne(Immeuble immeuble, Ascenseur ascenseur) {
        this.immeuble.set(immeuble);
        this.ascenseur.set(ascenseur);
    }

    public Immeuble getImmeuble() {
        return immeuble.get();
    }

    public ObjectProperty<Immeuble> immeubleProperty() {
        return immeuble;
    }

    public void setImmeuble(Immeuble immeuble) {
        this.immeuble.set(immeuble);
    }

    public Ascenseur getAscenseur() {
        return ascenseur.get();
    }

    public ObjectProperty<Ascenseur> ascenseurProperty() {
        return ascenseur;
    }

    public void setAscenseur(Ascenseur ascenseur) {
        this.ascenseur.set(ascenseur);
    }

    public TypeReparation getTypeReparation() {
        return typeReparation.get();
    }

    public ObjectProperty<TypeReparation> typeReparationProperty() {
        return typeReparation;
    }

    public void setTypeReparation(TypeReparation typeReparation) {
        this.typeReparation.set(typeReparation);
    }

    public int getAvancement() {
        return avancement.get();
    }

    public IntegerProperty avancementProperty() {
        return avancement;
    }

    public void setAvancement(int avancement) {
        this.avancement.set(avancement);
    }

    @Override
    public String toString() {
        return ascenseur.get() + "\n" + immeuble.get();
    }
}
