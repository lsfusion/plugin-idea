package com.lsfusion.lang.classes.link;

public class JSONLinkClass extends StaticFormatLinkClass {

    public final static JSONLinkClass instance = new JSONLinkClass();

    @Override
    public String getCaption() {
        return "Ссылка на файл JSON";
    }

    @Override
    public String getName() {
        return "JSONLINK";
    }
}