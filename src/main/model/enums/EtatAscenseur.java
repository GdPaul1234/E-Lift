package main.model.enums;

import java.util.Arrays;

/**
 * 
 */
public enum EtatAscenseur {
    EnPanne("-fx-text-fill: derive(red, 20%)"),
    EnPannePersonnes("-fx-text-fill: derive(red, 20%)"),
    EnCoursDeReparation("-fx-text-fill: orange"),
    EnService("-fx-text-fill: lightgreen");

    private static EtatAscenseur[] values = EtatAscenseur.EnPanne.getDeclaringClass().getEnumConstants();
    public final String style;

    EtatAscenseur(String style) {
        this.style = style;
    }

    public static EtatAscenseur[] getValues() {
        return values;
    }

    public static EtatAscenseur get(String state) {
        return Arrays.stream(getValues()).filter(v -> v.toString().equals(state)).findFirst().orElse(null);
    }
}