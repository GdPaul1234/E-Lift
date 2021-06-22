package main.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 */
public class Adresse {

    private StringProperty rue = new SimpleStringProperty(null, "rue");
    private StringProperty ville = new SimpleStringProperty(null, "ville");
    private StringProperty codePostal = new SimpleStringProperty(null, "CP");
    private FloatProperty longitude = new SimpleFloatProperty(null, "longitude");
    private FloatProperty latitude = new SimpleFloatProperty(null, "latitude");

    public Adresse(String rue, String ville, String codePostal, float longitude, float latitude) {
        this.rue.set(rue);
        this.ville.set(ville);
        this.codePostal.set(codePostal);
        this.longitude.set(longitude);
        this.latitude.set(latitude);
    }

    public String getRue() {
        return rue.get();
    }

    public String getVille() {
        return ville.get();
    }

    public String getCodePostal() {
        return codePostal.get();
    }

    public  float getLatitude() {
        return latitude.get();
    }

    public float getLongitude() {
        return longitude.get();
    }

    /* Property getter */
    public StringProperty rueProperty() {
        return rue;
    }

    public StringProperty villeProperty() {
        return ville;
    }

    public StringProperty codePostalProperty() {
        return codePostal;
    }

    public FloatProperty longitudeProperty() {
        return longitude;
    }

    public FloatProperty latitudeProperty() {
        return latitude;
    }
}