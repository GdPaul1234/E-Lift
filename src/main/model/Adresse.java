package main.model;

/**
 * 
 */
public class Adresse {

    private String rue;
    private String ville;
    private String codePostal;
    private String geolocalisation;

    public Adresse(String rue, String ville, String codePostal, String geolocalisation) {
        this.rue = rue;
        this.ville = ville;
        this.codePostal = codePostal;
        this.geolocalisation = geolocalisation;
    }

    public Adresse(String rue, String ville, String cp, float longitude, float lattitude) {
    }

    public String getRue() {
        return rue;
    }

    public String getVille() {
        return ville;
    }

    public String getCodePostal() {
        return codePostal;
    }

    public String getGeolocalisation() {
        return geolocalisation;
    }
}