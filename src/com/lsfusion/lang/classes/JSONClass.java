package com.lsfusion.lang.classes;

public class JSONClass extends DataClass {

    public final static JSONClass instance = new JSONClass();

    public DataClass op(DataClass compClass, boolean or, boolean string) {
        if (!(compClass instanceof JSONClass))
            return null;

        if(compClass.equals(this))
            return this;

        return JSONClass.instance;
    }

    @Override
    public String getName() {
        return "JSON";
    }

    @Override
    public String getCaption() {
        return "JSON";
    }
}