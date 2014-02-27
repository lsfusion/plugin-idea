package com.lsfusion.classes;

public class IntegerClass extends IntegralClass {

    int getWhole() {
        return 8;
    }

    int getPrecision() {
        return 0;
    }

    public boolean equals(Object obj) {
        return obj instanceof IntegerClass;
    }

    public int hashCode() {
        return 7;
    }

    public String getName() {
        return "INTEGER";
    }
}
