package model;

import model.enums.EtatOccupation;

import java.util.Set;

public class Ascensoriste extends Personne {

    private String localisation;
    private EtatOccupation state;
    public Set<Reparation> planning;

    public Ascensoriste(int idPersonne, String nom, String prenom, String telephone, String localisation, EtatOccupation state, Set<Reparation> planning) {
        super(idPersonne, nom, prenom, telephone);
        this.localisation = localisation;
        this.state = state;
        this.planning = planning;
    }

    /**
     * @param ascenseur
     */
    public void reparerAscenseur(Ascenseur ascenseur) {
        // TODO implement here
    }

    /**
     * @return
     */
    public EtatOccupation getState() {
        // TODO implement here
        return null;
    }

    /**
     * @param state
     */
    public void setState(EtatOccupation state) {
        // TODO implement here
    }

    /**
     * @param observer
     */
    public void attach(AscensoristeObserver observer) {
        // TODO implement here
    }


}