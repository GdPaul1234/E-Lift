package main.controller.DAO;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import main.model.Adresse;
import main.model.Ascenseur;
import main.model.Immeuble;
import main.model.enums.EtatAscenseur;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.sql.SQLException;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class AscenseurDAOTest {

    private static final ObjectProperty<Immeuble> immeuble = new SimpleObjectProperty<>(
            new Immeuble("a", 2,
                    new Adresse("a", "b", "c", 1, 1))
    );
    private static final ObjectProperty<Ascenseur> ascenseur = new SimpleObjectProperty<>(
            new Ascenseur("marque", "modele", new Date(), -1, EtatAscenseur.EnService)
    );
    private static final IntegerProperty idAscenseur = new SimpleIntegerProperty(-1);

    @Test
    @Order(1)
    void addAscenseurTest() {
        try {
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");
            ImmeubleDAO immeubleDAO = new ImmeubleDAO();
            AscenseurDAO ascenseurDAO = new AscenseurDAO();

            immeubleDAO.addImmeuble(immeuble.get());

            Immeuble addedImmeuble = immeubleDAO.findImmeubleByLocation(1, 1);
            assertNotNull(addedImmeuble);
            assertTrue(addedImmeuble.isValid());

            int idImmeuble = addedImmeuble.getIdImmeuble();
            immeuble.set(addedImmeuble);

            // ajout ascenseur valide
            synchronized (idAscenseur) {
                try {
                    idAscenseur.set(ascenseurDAO.addAscenseur(ascenseur.get(), idImmeuble));
                    List<Ascenseur> ascenseurs = ascenseurDAO.getAscenseursImmeuble(idImmeuble);
                    assertTrue(ascenseurs.contains(ascenseur.get()));
                } catch (SQLException e) {
                    fail(e.getMessage());
                }
            }

            // ajout ascenseur invalide
            Ascenseur badAscenseur = new Ascenseur("marque", "modele", new Date(), 5, EtatAscenseur.EnService);
            try {
                ascenseurDAO.addAscenseur(badAscenseur, addedImmeuble.getIdImmeuble());
                fail("add ascenseur should fail !");
            } catch (SQLException e) {
                /* Success */
                List<Ascenseur> ascenseurs = ascenseurDAO.getAscenseursImmeuble(idImmeuble);
                assertFalse(ascenseurs.contains(badAscenseur));
            }

            dataAccess.close();

        } catch (SQLException e) {
            fail(e.getMessage());
        }


    }

    @Test
    @Order(2)
    void getAscenseurTest() {
        try {
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");
            AscenseurDAO ascenseurDAO = new AscenseurDAO();

            synchronized (idAscenseur) {
                assertNotEquals(-1, idAscenseur.get());
                assertNotNull(ascenseurDAO.getAscenseur(idAscenseur.get()));

                Immeuble testImmeuble = ascenseurDAO.getImmeubleAscenseur(idAscenseur.get());
                assertNotNull(testImmeuble);

                List<Ascenseur> testAscenseurList = ascenseurDAO.getAscenseursImmeuble(testImmeuble.getIdImmeuble());
                assertTrue(testAscenseurList.size() > 0);
            }

            dataAccess.close();

        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Order(3)
    void editAscenseurTest() {
        try {
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");
            AscenseurDAO ascenseurDAO = new AscenseurDAO();
            int idImmeuble = immeuble.get().getIdImmeuble();

            synchronized (idAscenseur) {
                // modifier ascenseur valide
                try {
                    Ascenseur editedAscenseur = new Ascenseur("marque", "new Modèle !", new Date(), -1, EtatAscenseur.EnService);
                    ascenseurDAO.editAscenseur(idAscenseur.get(), editedAscenseur);

                    // vérif modification
                    Ascenseur editCommittedAscenseur = ascenseurDAO.getAscenseur(idAscenseur.get());
                    assertNotEquals("modele", editCommittedAscenseur.getModele());
                    assertEquals("new Modèle !", editCommittedAscenseur.getModele());
                } catch (SQLException e) {
                    fail(e.getMessage());
                }

                // modifier ascenseur invalide
                Ascenseur badAscenseur = new Ascenseur("marque", "modele", new Date(), 23, EtatAscenseur.EnService);
                try {
                    ascenseurDAO.editAscenseur(idAscenseur.get(), badAscenseur);
                    fail("add ascenseur should fail !");
                } catch (SQLException e) {
                    /* Success */
                    List<Ascenseur> testAscenseurs = ascenseurDAO.getAscenseursImmeuble(idImmeuble);
                    assertFalse(testAscenseurs.contains(badAscenseur));
                }

            }

            dataAccess.close();

        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }

    @Test
    @Order(4)
    void removeAscenseurTest() {
        try {
            DataAccess dataAccess = DataAccess.getInstance("personne.test639", "pwd");
            ImmeubleDAO immeubleDAO = new ImmeubleDAO();
            AscenseurDAO ascenseurDAO = new AscenseurDAO();
            int idImmeuble = immeuble.get().getIdImmeuble();

            // supprimer ascenseur
            synchronized (idAscenseur) {
                try {
                    ascenseurDAO.removeAscenseur(idAscenseur.get());

                    // vérif suppression
                    List<Ascenseur> editedAscenseurs = ascenseurDAO.getAscenseursImmeuble(idImmeuble);
                    assertTrue(editedAscenseurs.stream().noneMatch(v -> v.getIdAscenseur() == idAscenseur.get()));
                } catch (SQLException e) {
                    fail(e.getMessage());
                }
            }


            try {
                // supression auto immeuble
                Immeuble addedImmeuble = immeubleDAO.findImmeubleByLocation(1, 1);
                int addIdAscenseur = ascenseurDAO.addAscenseur(ascenseur.get(), idImmeuble);
                immeubleDAO.removeImmeuble(addedImmeuble.getIdImmeuble());

                // vérif suppression
                List<Ascenseur> editedAscenseurs = ascenseurDAO.getAscenseursImmeuble(idImmeuble);
                assertTrue(editedAscenseurs.stream().noneMatch(v -> v.getIdAscenseur() == addIdAscenseur));
            } catch (SQLException e) {
                fail(e.getMessage());
            }
            dataAccess.close();

        } catch (SQLException e) {
            fail(e.getMessage());
        }
    }
}