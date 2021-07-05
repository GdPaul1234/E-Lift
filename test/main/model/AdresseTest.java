package main.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class AdresseTest {

    @Test
    void isValidTest() {
        assertTrue(new Adresse("rue", "ville", "codePostal", 1.0f, 2.0f).isValid());

        Adresse testNewAdresse = new Adresse();
        testNewAdresse.rueProperty().set("rue");
        testNewAdresse.villeProperty().set("ville");
        testNewAdresse.codePostalProperty().set("cp");
        testNewAdresse.latitudeProperty().set(1.0f);
        testNewAdresse.longitudeProperty().set(1.0f);

        assertTrue(testNewAdresse.isValid());
    }

    @Test
    void isNotValidTest() {
        assertFalse(new Adresse().isValid());

        Adresse testNewAdresse = new Adresse();
        testNewAdresse.rueProperty().set("rue");
        testNewAdresse.villeProperty().set("");
        testNewAdresse.codePostalProperty().set(null);
        testNewAdresse.latitudeProperty().set(1.0f);
        testNewAdresse.longitudeProperty().set(1.0f);
        assertFalse(testNewAdresse.isValid());
    }
}