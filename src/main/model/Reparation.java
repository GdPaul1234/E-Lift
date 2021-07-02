package main.model;

import javafx.beans.property.*;
import main.model.enums.TypeReparation;
import main.model.interfaces.PlanningRessource;

import java.util.Date;

/**
 *
 */
public class Reparation implements PlanningRessource {

    private final ObjectProperty<Ascenseur> ascenseur = new SimpleObjectProperty<>(null, "ascenseur");
    private final ObjectProperty<Date> datePanne = new SimpleObjectProperty<>(null, "date panne");
    private final ObjectProperty<TypeReparation> type = new SimpleObjectProperty<>(null, "type réparation");
    private final IntegerProperty duree = new SimpleIntegerProperty(null, "durée");

    private final StringProperty loginAscensoriste = new SimpleStringProperty(null, "loginAscensoriste");
    private final StringProperty commentaire = new SimpleStringProperty(null, "commentaire");

    private final ObjectProperty<Intervention> intervention = new SimpleObjectProperty<>(null, "intervention");
    private final ObjectProperty<TrajetAller> trajetAller = new SimpleObjectProperty<>(null, "trajet");
    private final ObjectProperty<Immeuble> immeuble = new SimpleObjectProperty<>(null, "immeuble");

    public Reparation(Ascenseur ascenseur, Date datePanne, TypeReparation type, int duree) {
        this.ascenseur.set(ascenseur);
        this.datePanne.set(datePanne);
        this.type.set(type);
        this.duree.set(duree);
    }

    public Immeuble getImmeuble() {
        return immeuble.get();
    }

    public void setImmeuble(Immeuble immeuble) {
        this.immeuble.set(immeuble);
    }

    public int getDuree() {
        return duree.get();
    }

    public void setDuree(int duree) {
        this.duree.set(duree);
    }

    public Ascenseur getAscenseur() {
        return ascenseur.get();
    }

    public String getLoginAscensoriste() {
        return loginAscensoriste.get();
    }

    public void setLoginAscensoriste(String loginAscensoriste) {
        this.loginAscensoriste.set(loginAscensoriste);
    }

    public Date getDatePanne() {
        return datePanne.get();
    }

    public TypeReparation getType() {
        return type.get();
    }

    public String getCommentaire() {
        return commentaire.get();
    }

    public void setCommentaire(String commentaire) {
        this.commentaire.set(commentaire);
    }

    public void setIntervention(Intervention intervention) {
        this.intervention.set(intervention);
    }

    public TrajetAller getTrajetAller() {
        return trajetAller.get();
    }

    public void setTrajetAller(TrajetAller trajetAller) {
        this.trajetAller.set(trajetAller);
    }


    /* property getter */

    public ObjectProperty<Immeuble> immeubleProperty() {
        return immeuble;
    }

    public ObjectProperty<Intervention> interventionProperty() {
        return intervention;
    }

    public ObjectProperty<TrajetAller> trajetAllerProperty() {
        return trajetAller;
    }

    public ObjectProperty<Ascenseur> ascenseurProperty() {
        return ascenseur;
    }

    public StringProperty loginAscensoristeProperty() {
        return loginAscensoriste;
    }

    public ObjectProperty<Date> datePanneProperty() {
        return datePanne;
    }

    public ObjectProperty<TypeReparation> typeProperty() {
        return type;
    }

    public StringProperty commentaireProperty() {
        return commentaire;
    }

    public IntegerProperty dureeProperty() {
        return duree;
    }

}