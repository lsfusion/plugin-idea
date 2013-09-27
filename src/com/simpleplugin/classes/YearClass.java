package com.simpleplugin.classes;

public class YearClass extends IntegerClass {

    @Override
    public boolean equals(Object obj) {
        return obj instanceof YearClass;
    }

    @Override
    public int hashCode() {
        return 3233;
    }

    @Override
    public String toString() {
        return "YEAR";
    }
}
