package main.model;

import java.util.Set;

public class Immeuble {

    private int idImmeuble;
    private String nom;
    private int nbEtage;
    private Set<ContratMaintenance> contrats;
    private Adresse adresse;
    private Set<Ascenseur> ascenseurs;

    public Immeuble(String nom,int idImmeuble, int nbEtage) {
        this.nom = nom;
        this.idImmeuble = idImmeuble;
        this.nbEtage = nbEtage;
    }

    public int getIdImmeuble() {
        return idImmeuble;
    }

    public String getNom() {
        return nom;
    }

    public int getNbEtage() {
        return nbEtage;
    }

    public Set<ContratMaintenance> getContrats() {
        return contrats;
    }

    public Adresse getAdresse() {
        return adresse;
    }

    public Set<Ascenseur> getAscenseurs() {
        return ascenseurs;
    }

    public void setIdImmeuble(int idImmeuble) {
        this.idImmeuble = idImmeuble;
    }
}