package main.model;

import javafx.beans.property.*;
import main.model.enums.EtatAscenseur;
import main.model.interfaces.Ressource;

import java.util.Date;
import java.util.Set;

/**
 * A
 */
public class Ascenseur implements Ressource {

    private final IntegerProperty idAscenseur = new SimpleIntegerProperty(null, "ID");
    private final StringProperty marque = new SimpleStringProperty(null, "marque");
    private final StringProperty modele = new SimpleStringProperty(null, "modèle");
    private final ObjectProperty<Date> dateMiseEnService = new SimpleObjectProperty<>(null, "date mise en service");
    private final IntegerProperty etage = new SimpleIntegerProperty(null, "étage");
    private final ObjectProperty<EtatAscenseur> state = new SimpleObjectProperty<>(null, "état");

    private Set<Entretien> entretiens;
    private Set<Alerte> alertes;

    public Ascenseur(String marque, String modele, Date dateMiseEnService, int etage, EtatAscenseur state) {
        this.marque.set(marque);
        this.modele.set(modele);
        this.dateMiseEnService.set(dateMiseEnService);
        this.etage.set(etage);
        this.state.set(state);
    }

    public Ascenseur() {

    }

    public boolean isValid() {
        return marque.isNotEmpty().and(modele.isNotEmpty())
                .and(dateMiseEnService.isNotNull())
                .and(etage.greaterThanOrEqualTo(-10)).and(state.isNotNull()).get();
    }

    /* Getters */
    public int getIdAscenseur() {
        return idAscenseur.get();
    }

    public void setIdAscenseur(int idAscenseur) {
        this.idAscenseur.set(idAscenseur);
    }

    public String getMarque() {
        return marque.get();
    }

    public String getModele() {
        return modele.get();
    }

    public Date getDateMiseEnService() {
        return dateMiseEnService.get();
    }

    public void setDateMiseEnService(Date date) {
        this.dateMiseEnService.set(date);
    }

    public int getEtage() {
        return etage.get();
    }

    /* Setters */

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

    @Override
    public String toString() {
        return marque.get() + " " + modele.get() + ", "
                + state.get() + " au " + etage.get() + "e étage";
    }
}