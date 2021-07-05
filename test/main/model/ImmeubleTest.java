package main.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ImmeubleTest {

    @Test
    void isValidTest() {
        assertTrue(new Immeuble("a",2, new Adresse("a","b","c",1,1)).isValid());

        Immeuble testImmeuble = new Immeuble();
        testImmeuble.nomProperty().set("a");
        testImmeuble.nbEtageProperty().set(5);
        testImmeuble.adresseProperty().set(new Adresse("a","b","c",1,1));
        assertTrue(testImmeuble.isValid());
    }

    @Test
    void isNotValidTest() {
        assertFalse(new Immeuble().isValid());

        Immeuble testImmeuble = new Immeuble();
        testImmeuble.nomProperty().set("a");
        testImmeuble.nbEtageProperty().set(-11);
        testImmeuble.adresseProperty().set(new Adresse("a","b","c",1,1));
        assertFalse(testImmeuble.isValid());
    }
}