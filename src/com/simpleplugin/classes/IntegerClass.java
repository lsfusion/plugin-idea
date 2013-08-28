package com.simpleplugin.classes;

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
}
