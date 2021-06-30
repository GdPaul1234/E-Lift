package main.model;

import main.model.interfaces.Ressource;

import java.util.Observable;
import java.util.Observer;
import java.util.Set;

/**
 * 
 */
public class Gestionnaire extends Personne {

    private Set<Immeuble> immeubles;

    public Gestionnaire(String nom, String prenom, String telephone) {
        super(nom, prenom, telephone);
    }

    public Gestionnaire(Personne personne) {
        super(personne);
    }

    public Gestionnaire() {
        super();
    }
}