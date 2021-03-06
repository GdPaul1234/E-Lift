package main.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

/**
 * 
 */
public class Adresse {

    private final StringProperty rue = new SimpleStringProperty(null, "rue");
    private final StringProperty ville = new SimpleStringProperty(null, "ville");
    private final StringProperty codePostal = new SimpleStringProperty(null, "CP");
    private final FloatProperty longitude = new SimpleFloatProperty(null, "longitude");
    private final FloatProperty latitude = new SimpleFloatProperty(null, "latitude");

    public Adresse(String rue, String ville, String codePostal, float latitude, float longitude) {
        this.rue.set(rue);
        this.ville.set(ville);
        this.codePostal.set(codePostal);
        this.longitude.set(longitude);
        this.latitude.set(latitude);
    }

    public Adresse() {

    }

    public boolean isValid() {
        return rue.isNotEmpty().and(ville.isNotEmpty()).and(codePostal.isNotEmpty()).get();
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

    @Override
    public String toString() {
        return rue.get() + ", " + codePostal.get() + " " + ville.get();
    }
}