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
    protected String getName() {
        return "YEAR";
    }
}
