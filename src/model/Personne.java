package model;

public class Personne {

    private int idPersonne;
    private String nom;
    private String prenom;
    private String telephone;

    public Personne(int idPersonne, String nom, String prenom, String telephone) {
        this.idPersonne = idPersonne;
        this.nom = nom;
        this.prenom = prenom;
        this.telephone = telephone;
    }

    public int getIdPersonne() {
        return idPersonne;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getTelephone() {
        return telephone;
    }
}