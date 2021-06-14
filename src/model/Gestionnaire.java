package model;

import model.Ascenseur;
import model.Immeuble;
import model.PanneObserver;
import model.Personne;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * 
 */
public class Gestionnaire extends Personne implements Observer {

    private Set<Gestionnaire> gestionnaires;
    private Set<Immeuble> immeubles;
    private Set<PanneObserver> panneObservers;

    public Gestionnaire(int idPersonne, String nom, String prenom, String telephone) {
        super(idPersonne, nom, prenom, telephone);
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