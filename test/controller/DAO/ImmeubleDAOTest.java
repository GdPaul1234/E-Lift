package controller.DAO;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import main.controller.DAO.DataAccess;
import main.controller.DAO.ImmeubleDAO;
import main.model.Adresse;
import main.model.Immeuble;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImmeubleDAOTest {
    private static ObjectProperty<Adresse> testAdresse = new SimpleObjectProperty<>();


    @Test
    public void testGetAllImmeubles() {
        try {
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");

            List<Immeuble> immeubles = new ImmeubleDAO().getAllImmeubles();
            immeubles.forEach(v -> assertTrue(v.isValid()));

            dataAccess.close();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetMyImmeuble() {
        try {
            // using gestionnaire account
            DataAccess dataAccess = DataAccess.getInstance("pierre.durant941", "pwd");
            List<Immeuble> immeubles = new ImmeubleDAO().getMyImmeubles();
            // Nous assumons que Pierre Durant est un bon client
            immeubles.forEach(v -> assertTrue(v.isValid()));
            assertTrue(immeubles.size() > 0);

            List<Immeuble> allImmeubles = new ImmeubleDAO().getAllImmeubles();
            assertTrue(immeubles.size() <= allImmeubles.size());
            dataAccess.close();

        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testAddImmeubleNotGestionnaire() {
        testAdresse.set(new Adresse("rue","ville","cp",-1,-1));
        Immeuble immeuble = new Immeuble("test", 1, testAdresse.get());

        try {
            // Fail (using no gestionnaire account)
            DataAccess dataAccess = DataAccess.getInstance("modele.employe@e-lift.fr", "pwd");

            new ImmeubleDAO().addImmeuble(immeuble);
            dataAccess.close();
            fail("Not allowed !");
        } catch (SQLException e) {
            /* Hourra */
        }
    }

    @Test
    @Order(1)
    public void testAddImmeubleGestionnaire() {
        testAdresse.set(new Adresse("rue","ville","cp",-1,-1));
        Immeuble immeuble = new Immeuble("test", 1, testAdresse.get());

        try {
            // Work (using gestionnaire account)
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");

            new ImmeubleDAO().addImmeuble(immeuble);

            // Personne Test a maintenant 1 immeuble
            List<Immeuble> immeubles = new ImmeubleDAO().getMyImmeubles();
            assertEquals(1, immeubles.size());

            dataAccess.close();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Order(2)
    public void testEditImmeuble() {
        try {
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");
            ImmeubleDAO immeubleDAO = new ImmeubleDAO();

            Immeuble oldImmeuble = immeubleDAO.findImmeubleByLocation(-1,-1);

            Immeuble newImmeuble = new Immeuble("test modified", 2, testAdresse.get());
            immeubleDAO.editImmeuble(oldImmeuble.getIdImmeuble(), newImmeuble);

            Immeuble modifiedImmeuble = immeubleDAO.findImmeubleByLocation(-1,-1);

            assertNotEquals(oldImmeuble.getNom(), modifiedImmeuble.getNom());
            assertEquals(newImmeuble.getNom(), modifiedImmeuble.getNom());

            dataAccess.close();
        } catch (SQLException e) {
            fail(e.getMessage());
        }

    }

    @Test
    @Order(3)
    public void deleteImmeuble() {
        try {
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");
            ImmeubleDAO immeubleDAO = new ImmeubleDAO();

            Immeuble immeuble = immeubleDAO.findImmeubleByLocation(-1,-1);
            int oldNbImmeuble = immeubleDAO.getMyImmeubles().size();

            immeubleDAO.removeImmeuble(immeuble.getIdImmeuble());

            int newNbImmeuble = immeubleDAO.getMyImmeubles().size();

            assertTrue(newNbImmeuble < oldNbImmeuble);

            dataAccess.close();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }


}
