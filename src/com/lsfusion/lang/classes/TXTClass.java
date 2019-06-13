package com.lsfusion.lang.classes;

public class TXTClass extends StaticFormatFileClass {

    public final static TXTClass instance = new TXTClass();

    @Override
    public String getCaption() {
        return "Text file";
    }

    @Override
    public String getName() {
        return "TEXTFILE";
    }
}