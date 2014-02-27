package com.lsfusion.classes;

public class DoubleClass extends IntegralClass {

    int getWhole() {
        return 99999;
    }

    int getPrecision() {
        return 99999;
    }

    public boolean equals(Object obj) {
        return obj instanceof DoubleClass;
    }

    public int hashCode() {
        return 6;
    }

    public String getName() {
        return "DOUBLE";
    }
}
