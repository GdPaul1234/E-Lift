package model;

import java.sql.Date;

/**
 * 
 */
public class Entretien {

    private Date dateEntretien;

    public Entretien(Date dateEntretien) {
        this.dateEntretien = dateEntretien;
    }

    public Date getDateEntretien() {
        return dateEntretien;
    }
}