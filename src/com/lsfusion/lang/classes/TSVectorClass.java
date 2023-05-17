package com.lsfusion.lang.classes;

public class TSVectorClass extends DataClass {
    public final static TSVectorClass instance = new TSVectorClass();

    @Override
    public String getName() {
        return "TSVECTOR";
    }

    @Override
    public String getCaption() {
        return "TSVECTOR";
    }

    @Override
    public DataClass op(DataClass compClass, boolean or, boolean string) {
        return compClass instanceof TSVectorClass ? this : null;
    }

    @Override
    protected int getDefaultCharWidth() {
        return 15;
    }
}
