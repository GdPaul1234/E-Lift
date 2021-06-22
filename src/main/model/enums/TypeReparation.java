package main.model.enums;

import java.util.Arrays;

public enum TypeReparation {
    BlocageDesDortesDAscenseur,
    SurchargeDeLAscenseur,
    PannesDesCartesElectronique,
    PannesCauseAuConditionClimatique,
    CoupuresElectrique;

    public static TypeReparation[] getValues() {
        return TypeReparation.BlocageDesDortesDAscenseur.getDeclaringClass().getEnumConstants();
    }

    public static TypeReparation get(String reparation) {
        return Arrays.stream(getValues()).filter(v -> v.toString().equals(reparation)).findFirst().orElse(null);
    }
}
