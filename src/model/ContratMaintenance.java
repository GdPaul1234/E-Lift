package model;


import java.sql.Date;

/**
 * 
 */
public class ContratMaintenance {

    private int idContrat;
    private String nom;
    private Date debut;
    private Date fin;
    public ContratMaintenance(int idContrat, String nom, Date debut, Date fin) {
        this.idContrat = idContrat;
        this.nom = nom;
        this.debut = debut;
        this.fin = fin;
    }

    public int getIdContrat() {
        return idContrat;
    }

    public String getNom() {
        return nom;
    }

    public Date getDebut() {
        return debut;
    }

    public Date getFin() {
        return fin;
    }
}