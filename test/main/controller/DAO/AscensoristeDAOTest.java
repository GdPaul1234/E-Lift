package main.controller.DAO;

import main.model.*;
import main.model.enums.EtatAscenseur;
import main.model.enums.TypeReparation;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AscensoristeDAOTest {

    @Test
void findFailAvailableAscensoristeAscensoriste() {
        try {
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");
            Ascenseur ascenseur = new Ascenseur();
            ascenseur.setIdAscenseur(9);

            Immeuble immeuble = new Immeuble();
            immeuble.setAdresse(new Adresse("32 Avenue de la République","Villejuif","94800",48.7886f,2.36399f));

            TypeReparation typeReparation = TypeReparation.ConditionClimatique;

            Reparation reparation = new Reparation(ascenseur, new Date(2021, Calendar.JULY, 4,11,8,5), typeReparation,typeReparation.duree);
            reparation.setImmeuble(immeuble);

            Ascensoriste ascensoriste = new AscensoristeDAO().findAvailableAscensoriste(reparation);

            assertNull(ascensoriste);

            dataAccess.close();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }
}