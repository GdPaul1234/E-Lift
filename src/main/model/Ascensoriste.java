package main.model;

import main.model.enums.EtatOccupation;

import java.util.Set;

public class Ascensoriste extends Personne {

    private String localisation;
    public Set<Reparation> planning;

    public Ascensoriste(String nom, String prenom, String telephone, String localisation) {
        super(nom, prenom, telephone);
        this.localisation = localisation;
    }

    public Ascensoriste(Personne personne) {
        super(personne);
    }

    /**
     * @param ascenseur
     */
    public void reparerAscenseur(Ascenseur ascenseur) {
        // TODO implement here
    }


    /**
     * @param observer
     */
    public void attach(AscensoristeObserver observer) {
        // TODO implement here
    }
}