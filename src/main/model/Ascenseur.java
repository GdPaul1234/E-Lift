package main.model;

import main.model.enums.EtatAscenseur;
import main.model.enums.TypeReparation;

import java.util.Date;
import java.util.Set;

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
    private TypeReparation reparation;
    private Set<Entretien> entretiens;
    private Set<Alerte> alertes;

    public Ascenseur(int idAscenseur, String marque, String modele, Date dateMiseEnService, int etage, EtatAscenseur state, TypeReparation reparation) {
        this.idAscenseur = idAscenseur;
        this.marque = marque;
        this.modele = modele;
        this.dateMiseEnService = dateMiseEnService;
        this.etage = etage;
        this.state = state;
        this.reparation = reparation;
    }

    public int getIdAscenseur() { return idAscenseur; }

    public String getMarque() {
        return marque;
    }

    public String getModele() {
        return modele;
    }

    public Date getDateMiseEnService() {
        return dateMiseEnService;
    }

    public int getEtage() { return etage; }

    public EtatAscenseur getEtatAscenceur() { return state; }

    public TypeReparation getTypeReparation() { return reparation; }

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