package main.model;

import java.util.Date;
import java.util.Calendar;


public class ContratMaintenance {

    private Date debut;
    private Date fin;

    public ContratMaintenance(Date debut) {
        this.debut = debut;
        if (debut != null) {
            Calendar dateFin = Calendar.getInstance();
            dateFin.setTime(debut);
            dateFin.add(Calendar.YEAR, 1);
            fin = dateFin.getTime();
        }
    }

    public Date getDebut() {
        return debut;
    }

    public Date getFin() {
        return fin;
    }
}