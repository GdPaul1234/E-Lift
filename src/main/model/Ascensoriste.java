package main.model;

public class Ascensoriste extends Personne {

    private float longitude, latitude;

    public Ascensoriste(String nom, String prenom, String telephone, float latitude, float longitude) {
        super(nom, prenom, telephone);
        this.longitude = longitude;
        this.latitude = latitude;
    }

    public Ascensoriste() {

    }

    public Ascensoriste(Personne personne) {
        super(personne);
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLatitude() {
        return latitude;
    }

}