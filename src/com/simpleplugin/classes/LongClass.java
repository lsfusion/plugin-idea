package com.simpleplugin.classes;

public class LongClass extends IntegralClass {

    int getWhole() {
        return 10;
    }

    int getPrecision() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LongClass;
    }

    @Override
    public int hashCode() {
        return 9;
    }

    protected String getName() {
        return "LONG";
    }
}
