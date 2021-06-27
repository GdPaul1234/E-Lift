package main.model;

import main.model.interfaces.Ressource;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * 
 */
public class Gestionnaire extends Personne implements Observer {

    private Set<Immeuble> immeubles;
    private Set<PanneObserver> panneObservers;

    public Gestionnaire(String nom, String prenom, String telephone) {
        super(nom, prenom, telephone);
    }

    public Gestionnaire(Personne personne) {
        super(personne);
    }

    public Gestionnaire() {
        super();
    }


    /**
     * @param ascenseur
     */
    public void demanderReparationAscenseur(Ascenseur ascenseur) {
        // TODO implement here
    }

    /**
     * @param ascenseur
     */
    public void retirerPanneAscenseur(Ascenseur ascenseur) {
        // TODO implement here
    }


    @Override
    public void update(Observable o, Object arg) {

    }
}