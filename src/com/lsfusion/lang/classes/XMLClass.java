package com.lsfusion.lang.classes;

public class XMLClass extends DataClass {

    public final static XMLClass instance = new XMLClass();

    @Override
    public String getName() {
        return "XML";
    }

    @Override
    public String getCaption() {
        return "XML";
    }
}
