package main.model.enums;

import java.util.Arrays;

public enum TypeReparation {
    BlocageDesPortes(30),
    Surcharge(20),
    CarteElectronique(40),
    ConditionClimatique(90),
    CoupuresElectrique(110);

    public final int duree;

    TypeReparation(int duree) {
        this.duree = duree;
    }

    public static TypeReparation[] getValues() {
        return TypeReparation.BlocageDesPortes.getDeclaringClass().getEnumConstants();
    }

    public static TypeReparation get(String reparation) {
        return Arrays.stream(getValues()).filter(v -> v.toString().equals(reparation)).findFirst().orElse(null);
    }
}
