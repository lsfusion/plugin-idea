package com.lsfusion.lang.classes.link;

public class JSONLinkClass extends StaticFormatLinkClass {

    public final static JSONLinkClass instance = new JSONLinkClass();

    @Override
    public String getCaption() {
        return "JSON link";
    }

    @Override
    public String getName() {
        return "JSONLINK";
    }
}