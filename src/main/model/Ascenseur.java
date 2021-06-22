package main.model;

import javafx.beans.property.*;
import main.model.enums.EtatAscenseur;
import main.model.enums.TypeReparation;

import java.util.Date;
import java.util.Set;

/**
 * A
 */
public class Ascenseur {

    private IntegerProperty idAscenseur = new SimpleIntegerProperty(null,"ID");
    private StringProperty marque = new SimpleStringProperty(null, "marque");
    private StringProperty modele = new SimpleStringProperty(null, "modèle");
    private ObjectProperty<Date> dateMiseEnService = new SimpleObjectProperty<>(null, "date mise en service");
    private IntegerProperty etage = new SimpleIntegerProperty(null, "étage");
    private ObjectProperty<EtatAscenseur> state = new SimpleObjectProperty<>(null, "état");

    private Set<Entretien> entretiens;
    private Set<Alerte> alertes;

    public Ascenseur(int idAscenseur, String marque, String modele, Date dateMiseEnService, int etage, EtatAscenseur state) {
        this.idAscenseur.set(idAscenseur);
        this.marque.set(marque);
        this.modele.set(modele);
        this.dateMiseEnService.set(dateMiseEnService);
        this.etage.set(etage);
        this.state.set(state);
    }

    /* Getters */
    public int getIdAscenseur() { return idAscenseur.get(); }

    public String getMarque() {
        return marque.get();
    }

    public String getModele() {
        return modele.get();
    }

    public Date getDateMiseEnService() {
        return dateMiseEnService.get();
    }

    public int getEtage() { return etage.get(); }

    public EtatAscenseur getState() {
        return state.get();
    }

    public void setState(EtatAscenseur state) {
        this.state.set(state);
    }

    /**
     * @param observer
     */
    public void attach(PanneObserver observer) {
        // TODO implement here
    }

    /* Property getter */

    public IntegerProperty idAscenseurProperty() {
        return idAscenseur;
    }

    public StringProperty marqueProperty() {
        return marque;
    }

    public StringProperty modeleProperty() {
        return modele;
    }

    public ObjectProperty<Date> dateMiseEnServiceProperty() {
        return dateMiseEnService;
    }

    public IntegerProperty etageProperty() {
        return etage;
    }

    public ObjectProperty<EtatAscenseur> stateProperty() {
        return state;
    }
}