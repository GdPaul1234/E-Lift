package controller.DAO;

import main.controller.DAO.DataAccess;
import main.controller.DAO.ImmeubleDAO;
import main.model.Immeuble;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ImmeubleTest {
    private static DataAccess dataAccess;
    private static ImmeubleDAO immeubleDAO;

    @BeforeAll
    static void setUpBeforeClass() {
        try {
            dataAccess = DataAccess.getInstance("root", "ultrasecurepwd");
            assertNotNull(dataAccess.getConnection());

            immeubleDAO = new ImmeubleDAO();
        } catch (IllegalArgumentException | SQLException e) {
            fail(e.getMessage());
        }

    }

    @Test
    public void testGetAllImmeubles() {
        try {
            List<Immeuble> immeubles = immeubleDAO.getAllImmeubles();

            for(Immeuble immeuble : immeubles) {
                assertTrue(immeuble.isValid());
            }
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void testGetMyImmeuble() {
        try {
            synchronized (ImmeubleTest.dataAccess) {
                // using gestionnaire account
                DataAccess dataAccess = DataAccess.getInstance("pierre.durant941", "pwd");
                List<Immeuble> immeubles = new ImmeubleDAO().getMyImmeubles();
                // Nous assumons que Pierre Durant est un bon client
                assertTrue(immeubles.size() > 0);
                dataAccess.close();

                // using bad gestionnaire
                dataAccess = DataAccess.getInstance("personne.test639", "pwd");
                immeubles = new ImmeubleDAO().getMyImmeubles();
                // Nous assumons que Pierre Durant est un bon client
                assertEquals(immeubles.size(), 0);
                dataAccess.close();
            }

            // Restore previous db connection
            setUpBeforeClass();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @AfterAll
    static void tearDownAfterClass() {
        assertNotNull(dataAccess.getConnection());
        try {
            dataAccess.close();
        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

}
