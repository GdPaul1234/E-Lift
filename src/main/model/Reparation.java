package main.model;

import main.model.enums.TypeReparation;

import java.util.Date;

/**
 * 
 */
public class Reparation {

    private int idAscenseur;
    private String login;
    private Date dateReparation;
    private TypeReparation type;
    private String commentaire;
    private String avancement;

    public Reparation(int idAscenceur, String login, Date dateReparation, TypeReparation type, String commentaire,String avancement) {
        this.idAscenseur = idAscenseur;
        this.dateReparation = dateReparation;
        this.login = login;
        this.type = type;
        this.commentaire = commentaire;
        this.avancement = avancement;
    }

    public int getIdAscenseur() {
        return idAscenseur;
    }

    public String getLogin() {
        return login;
    }

    public Date getDateReparation() {
        return dateReparation;
    }

    public TypeReparation getType() {
        return type;
    }

    public String getCommentaire() {
        return commentaire;
    }

    public String getAvancement() {
        return avancement;
    }
}