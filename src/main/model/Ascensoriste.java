package main.model;

import java.util.Set;

public class Ascensoriste extends Personne {

    private float longitude, lattidude;
    public Set<Reparation> planning;

    public Ascensoriste(String nom, String prenom, String telephone, float longitude, float lattidude) {
        super(nom, prenom, telephone);
        this.longitude = longitude;
        this.lattidude = lattidude;
    }

    public Ascensoriste() {

    }

    public Ascensoriste(Personne personne) {
        super(personne);
    }

    public float getLongitude() {
        return longitude;
    }

    public float getLattidude() {
        return lattidude;
    }

    /**
     * @param ascenseur
     */
    public void reparerAscenseur(Ascenseur ascenseur) {
        // TODO implement here
    }


    /**
     * @param observer
     */
    public void attach(AscensoristeObserver observer) {
        // TODO implement here
    }
}