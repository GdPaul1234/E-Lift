package main.model;


import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Personne {

    private StringProperty login  = new SimpleStringProperty(null, "login");
    private StringProperty  nom = new SimpleStringProperty(null, "nom");
    private StringProperty  prenom = new SimpleStringProperty(null, "prenom");
    private StringProperty telephone = new SimpleStringProperty(null, "telephone");

    private StringProperty[] columnsHeader = { login, nom, prenom, telephone };

    public Personne(String nom, String prenom, String telephone) {
        this.nom.set(nom);
        this.prenom.set(prenom);
        this.telephone.set(telephone);
    }

    public Personne(Personne personne) {
        login = personne.login;
        nom = personne.nom;
        prenom = personne.prenom;
        telephone = personne.telephone;
        columnsHeader = personne.columnsHeader;
    }

    public boolean isValid() {
        return nom.isNotEmpty().get() && prenom.isNotEmpty().get() && telephone.isNotEmpty().get();
    }

    public String getLogin() {
        return login.get();
    }

    public String getNom() {
        return nom.get();
    }

    public String getPrenom() {
        return prenom.get();
    }

    public String getTelephone() {
        return telephone.get();
    }

    public void setLogin(String login) {
        this.login.set(login);
    }

    /* Property getter */
    public StringProperty loginProperty() {
        return login;
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public StringProperty prenomProperty() {
        return prenom;
    }

    public StringProperty telephoneProperty() {
        return telephone;
    }
}