package com.lsfusion.lang.classes;

public class JSONTextClass extends DataClass {

    public final static JSONTextClass instance = new JSONTextClass();

    @Override
    public String getName() {
        return "JSONTEXT";
    }

    @Override
    public String getCaption() {
        return "JSON text";
    }
}
