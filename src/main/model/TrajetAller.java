package main.model;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

import java.util.Date;

public class TrajetAller {

    private ObjectProperty<Date> dateTrajet = new SimpleObjectProperty<>(null, "dateTrajet");
    private IntegerProperty dureeTrajet = new SimpleIntegerProperty(null, "dureeTrajet");
    private IntegerProperty codePostal = new SimpleIntegerProperty(null, "destImmeuble");


    public TrajetAller(Date dateTrajet, int dureeTrajet, int destImmeuble) {
        this.dateTrajet.set(dateTrajet);
        this.dureeTrajet.set(dureeTrajet);
        this.codePostal.set(destImmeuble);
    }

    public Date getDateTrajet() {
        return dateTrajet.get();
    }

    public void setDateTrajet(Date dateTrajet) {
        this.dateTrajet.set(dateTrajet);
    }

    public int getDureeTrajet() {
        return dureeTrajet.get();
    }

    public int getDestImmeuble() {
        return codePostal.get();
    }

}
