package main.model;

import javafx.beans.property.*;
import main.model.interfaces.Ressource;

import java.util.Set;

public class Immeuble implements Ressource {

    private int idImmeuble;
    private final StringProperty nom = new SimpleStringProperty(null, "nom");
    private final IntegerProperty nbEtage = new SimpleIntegerProperty(null, "nb étage");
    private final ObjectProperty<Adresse> adresse = new SimpleObjectProperty<>(null, "adresse");
    private Set<ContratMaintenance> contrats;
    private Set<Ascenseur> ascenseurs;

    public Immeuble(String nom, int nbEtage, Adresse adresse) {
        this.nom.set(nom);
        this.nbEtage.set(nbEtage);
        this.adresse.set(adresse);
    }

    public Immeuble() { }

    public boolean isValid() {
        return nom.isNotEmpty().and(nbEtage.greaterThanOrEqualTo(1)).and(adresse.isNotNull()).get() && adresse.get().isValid();
    }

    public int getIdImmeuble() {
        return idImmeuble;
    }

    public String getNom() {
        return nom.get();
    }

    public int getNbEtage() {
        return nbEtage.get();
    }

    public Adresse getAdresse() {
        return adresse.get();
    }

    public Set<ContratMaintenance> getContrats() {
        return contrats;
    }

    public Set<Ascenseur> getAscenseurs() {
        return ascenseurs;
    }

    /* Setter */

    public void setIdImmeuble(int idImmeuble) {
        this.idImmeuble = idImmeuble;
    }


    public void setAdresse(Adresse adresse) {
        this.adresse.set(adresse);
    }

    /* Property getter */
    public StringProperty nomProperty() {
        return nom;
    }

    public IntegerProperty nbEtageProperty() {
        return nbEtage;
    }

    public ObjectProperty<Adresse> adresseProperty() {
        return adresse;
    }

    @Override
    public String toString() {
        return nom.get() + ", " + adresse.get().getVille() ;
    }
}