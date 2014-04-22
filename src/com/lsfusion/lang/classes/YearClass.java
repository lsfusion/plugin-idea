package com.lsfusion.lang.classes;

public class YearClass extends IntegerClass {

    public final static YearClass instance = new YearClass();

    @Override
    public boolean equals(Object obj) {
        return obj instanceof YearClass;
    }

    @Override
    public int hashCode() {
        return 3233;
    }

    @Override
    public String getName() {
        return "YEAR";
    }

    @Override
    public String getCaption() {
        return "Год";
    }
}
