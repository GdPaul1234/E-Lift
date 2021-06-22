package main.model.enums;

import java.util.Arrays;

/**
 * 
 */
public enum EtatAscenseur {
    EnPanne,
    EnPannePersonnes,
    EnCoursDeReparation,
    EnService;

    public static EtatAscenseur[] getValues() {
        return EtatAscenseur.EnPanne.getDeclaringClass().getEnumConstants();
    }

    public static EtatAscenseur get(String state) {
        return Arrays.stream(getValues()).filter(v -> v.toString().equals(state)).findFirst().orElse(null);
    }
}