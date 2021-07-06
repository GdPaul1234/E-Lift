package main.controller.DAO;

import main.model.*;
import main.model.enums.EtatAscenseur;
import main.model.enums.TypeReparation;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

class AscensoristeDAOTest {

    @Test
    void findFailAvailableAscensoristeAscensoristeTest() {
        try {
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");
            Ascenseur ascenseur = new Ascenseur();
            ascenseur.setIdAscenseur(9);

            Immeuble immeuble = new Immeuble();
            immeuble.setAdresse(new Adresse("rtf", "kcyg", "94800", 48.7886f, 2.36399f));

            TypeReparation typeReparation = TypeReparation.ConditionClimatique;

            Reparation reparation = new Reparation(ascenseur, new Date(), typeReparation, typeReparation.duree);
            reparation.setImmeuble(immeuble);

            Ascensoriste ascensoriste = new AscensoristeDAO().findAvailableAscensoriste(reparation);
            assertNull(ascensoriste);

            dataAccess.close();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    void findAvailableAscensoristeAscensoristeTest() {
        try {
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");

            ImmeubleDAO immeubleDAO = new ImmeubleDAO();
            AscenseurDAO ascenseurDAO = new AscenseurDAO();
            ReparationDAO reparationDAO = new ReparationDAO();

            int nbOldReparation = reparationDAO.getMyPlanning().size();

            // ajout immeuble
            Immeuble immeuble = new Immeuble("a", 2,
                    new Adresse("a", "b", "c", 1, 1));

            immeubleDAO.addImmeuble(immeuble);
            Immeuble addedImmeuble = immeubleDAO.findImmeubleByLocation(1, 1);

            // ajout ascenseur valide en panne
            Ascenseur ascenseur = new Ascenseur("marque", "modele", new Date(), 1, EtatAscenseur.EnPanne);

            int idAscenseur = ascenseurDAO.addAscenseur(ascenseur, addedImmeuble.getIdImmeuble());

            int nbNewReparation = reparationDAO.getMyPlanning().size();

            assertTrue(nbNewReparation > nbOldReparation);

            // suppression auto immeuble
            immeubleDAO.removeImmeuble(addedImmeuble.getIdImmeuble());

            dataAccess.close();
        } catch (SQLException | ParseException e) {
            fail(e.getMessage());
        }
    }
}