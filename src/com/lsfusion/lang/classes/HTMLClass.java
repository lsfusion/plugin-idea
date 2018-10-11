package com.lsfusion.lang.classes;

public class HTMLClass extends StaticFormatFileClass {

    public final static HTMLClass instance = new HTMLClass();

    @Override
    public String getCaption() {
        return "Файл HTML";
    }

    @Override
    public String getName() {
        return "HTMLFILE";
    }
}

