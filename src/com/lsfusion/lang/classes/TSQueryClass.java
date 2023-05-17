package com.lsfusion.lang.classes;

public class TSQueryClass extends DataClass {
    public final static TSQueryClass instance = new TSQueryClass();

    @Override
    public String getName() {
        return "TSQUERY";
    }

    @Override
    public String getCaption() {
        return "TSQUERY";
    }

    @Override
    public DataClass op(DataClass compClass, boolean or, boolean string) {
        return compClass instanceof TSQueryClass ? this : null;
    }

    @Override
    protected int getDefaultCharWidth() {
        return 15;
    }
}
