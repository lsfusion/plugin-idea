package com.lsfusion.lang.classes;

public class DoubleClass extends IntegralClass {

    public final static DoubleClass instance = new DoubleClass();

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

    @Override
    public String getCaption() {
        return "Вещественное число";
    }
}
