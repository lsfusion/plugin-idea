package com.lsfusion.lang.classes;

public class RawClass extends StaticFormatFileClass {

    public final static RawClass instance = new RawClass();

    @Override
    public String getCaption() {
        return "Неизвестный файл";
    }

    @Override
    public String getName() {
        return "RAWFILE";
    }
}