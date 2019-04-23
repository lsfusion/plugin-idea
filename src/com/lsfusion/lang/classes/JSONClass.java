package com.lsfusion.lang.classes;

public class JSONClass extends StaticFormatFileClass {

    public final static JSONClass instance = new JSONClass();

    @Override
    public String getCaption() {
        return "JSON file";
    }

    @Override
    public String getName() {
        return "JSONFILE";
    }
}
