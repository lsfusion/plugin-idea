package com.lsfusion.lang.classes;

public class NamedFileClass extends AbstractDynamicFormatFileClass {

    public final static NamedFileClass instance = new NamedFileClass();

    @Override
    public String getCaption() {
        return "NamedFile";
    }

    @Override
    public String getName() {
        return "NAMEDFILE";
    }
}