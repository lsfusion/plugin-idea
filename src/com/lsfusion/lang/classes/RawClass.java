package com.lsfusion.lang.classes;

public class RawClass extends StaticFormatFileClass {

    public final static RawClass instance = new RawClass();

    @Override
    public String getCaption() {
        return "Raw file";
    }

    @Override
    public String getName() {
        return "RAWFILE";
    }
}