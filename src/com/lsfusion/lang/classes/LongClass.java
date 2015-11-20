package com.lsfusion.lang.classes;

public class LongClass extends IntClass {

    public final static LongClass instance = new LongClass();

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

    public String getName() {
        return "LONG";
    }

    @Override
    public String getCaption() {
        return "Длинное целое число";
    }

    @Override
    public String getPreferredMask() {
        return "9 999 999 999 999 999 999";
    }
}
