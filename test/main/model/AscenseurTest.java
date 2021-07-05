package main.model;

import main.model.enums.EtatAscenseur;
import org.junit.jupiter.api.Test;

import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class AscenseurTest {

    @Test
    void isValidTest() {
        assertTrue(new Ascenseur("marque", "modele", new Date(), 1, EtatAscenseur.EnService).isValid());

        Ascenseur testAscenseur = new Ascenseur();
        testAscenseur.marqueProperty().set("marque");
        testAscenseur.modeleProperty().set("modele");
        testAscenseur.dateMiseEnServiceProperty().set(new Date());
        testAscenseur.stateProperty().set(EtatAscenseur.EnService);
        assertTrue(testAscenseur.isValid());
    }

    @Test
    void isNotValidTest() {
        assertFalse(new Ascenseur().isValid());

        Ascenseur testAscenseur = new Ascenseur();
        testAscenseur.marqueProperty().set("marque");
        testAscenseur.modeleProperty().set("");
        testAscenseur.dateMiseEnServiceProperty().set(null);
        testAscenseur.stateProperty().set(EtatAscenseur.EnService);
        assertFalse(testAscenseur.isValid());
    }
}