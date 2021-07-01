package main.model;

import javafx.beans.property.*;
import main.model.enums.TypeReparation;
import main.model.interfaces.PlanningRessource;

import java.util.Date;

/**
 *
 */
public class Reparation implements PlanningRessource {

    private final IntegerProperty idAscenseur = new SimpleIntegerProperty(null, "ID");
    private final ObjectProperty<Date> datePanne = new SimpleObjectProperty<>(null, "date panne");
    private final ObjectProperty<TypeReparation> type = new SimpleObjectProperty<>(null, "type réparation");
    private final IntegerProperty duree = new SimpleIntegerProperty(null, "durée");

    private final StringProperty loginAscensoriste = new SimpleStringProperty(null, "loginAscensoriste");
    private final StringProperty commentaire = new SimpleStringProperty(null, "commentaire");
    private final StringProperty avancement = new SimpleStringProperty(null, "avancement");

    private final ObjectProperty<Intervention> intervention = new SimpleObjectProperty<>(null, "intervention");
    private final ObjectProperty<TrajetAller> trajetAller = new SimpleObjectProperty<>(null, "trajet");

    public Reparation(int idAscenceur, Date datePanne, TypeReparation type, int duree) {
        this.idAscenseur.set(idAscenceur);
        this.datePanne.set(datePanne);
        this.type.set(type);
        this.avancement.set("Demande réparation envoyée");
        this.duree.set(duree);
    }

    public int getDuree() {
        return duree.get();
    }

    public void setDuree(int duree) {
        this.duree.set(duree);
    }

    public int getIdAscenseur() {
        return idAscenseur.get();
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

    public String getAvancement() {
        return avancement.get();
    }

    public void setAvancement(String avancement) {
        this.avancement.set(avancement);
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
    public ObjectProperty<Intervention> interventionProperty() {
        return intervention;
    }

    public ObjectProperty<TrajetAller> trajetAllerProperty() {
        return trajetAller;
    }

    public IntegerProperty idAscenseurProperty() {
        return idAscenseur;
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

    public StringProperty avancementProperty() {
        return avancement;
    }

    public IntegerProperty dureeProperty() {
        return duree;
    }

}