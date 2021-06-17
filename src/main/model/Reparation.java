package main.model;

import main.model.enums.TypeReparation;

import java.util.*;

/**
 * 
 */
public class Reparation {

    private Date dateReparation;
    private TypeReparation type;
    private String commentaire;
    private Ascensoriste intervenant;

    public Reparation(Date dateReparation) {
        this.dateReparation = dateReparation;
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

    public Ascensoriste getIntervenant() {
        return intervenant;
    }
}