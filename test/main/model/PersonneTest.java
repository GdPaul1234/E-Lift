package main.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PersonneTest {

    @Test
    void isValidTest() {
        assertTrue(new Personne("a","b","01 23 45 67 89").isValid());

        Personne testPersonne = new Personne();
        testPersonne.nomProperty().set("a");
        testPersonne.prenomProperty().set("b");
        testPersonne.telephoneProperty().set("guk");
        assertTrue(testPersonne.isValid());

        Gestionnaire testGestionnaire = new Gestionnaire(testPersonne);
        assertTrue(testGestionnaire.isValid());

        Ascensoriste testAscensoriste = new Ascensoriste(testPersonne);
        assertTrue(testAscensoriste.isValid());
    }

    @Test
    void isNotValidTest() {
        assertFalse(new Personne().isValid());

        Personne testPersonne = new Personne();
        testPersonne.nomProperty().set("a");
        testPersonne.prenomProperty().set("b");
        testPersonne.telephoneProperty().set("");
        assertFalse(testPersonne.isValid());

        Gestionnaire testGestionnaire = new Gestionnaire(testPersonne);
        assertFalse(testGestionnaire.isValid());

        Ascensoriste testAscensoriste = new Ascensoriste(testPersonne);
        assertFalse(testAscensoriste.isValid());
    }
}