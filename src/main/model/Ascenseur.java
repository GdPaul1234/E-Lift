package main.model;

import main.model.enums.EtatAscenseur;

import java.util.*;

/**
 * A
 */
public class Ascenseur {

    private int idAscenseur;
    private String marque;
    private String modele;
    private Date dateMiseEnService;
    private int etage;
    private EtatAscenseur state;
    private Set<Entretien> entretiens;
    private Reparation reparation;
    private Set<Alerte> alertes;

    public Ascenseur(int idAscenseur, String marque, String modele, Date dateMiseEnService, int etage, EtatAscenseur state, Reparation reparation) {
        this.idAscenseur = idAscenseur;
        this.marque = marque;
        this.modele = modele;
        this.dateMiseEnService = dateMiseEnService;
        this.etage = etage;
        this.state = state;
        this.reparation = reparation;
    }

    /**
     * @return
     */
    public EtatAscenseur getState() {
        // TODO implement here
        return null;
    }

    /**
     * @param etat
     */
    public void setState(EtatAscenseur etat) {
        // TODO implement here
    }

    /**
     * @param observer
     */
    public void attach(PanneObserver observer) {
        // TODO implement here
    }


}