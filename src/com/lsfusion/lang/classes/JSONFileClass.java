package com.lsfusion.lang.classes;

public class JSONFileClass extends StaticFormatFileClass {

    public final static JSONFileClass instance = new JSONFileClass();

    @Override
    public String getCaption() {
        return "JSON file";
    }

    @Override
    public String getName() {
        return "JSONFILE";
    }
}
