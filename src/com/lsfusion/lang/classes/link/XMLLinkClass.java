package com.lsfusion.lang.classes.link;

public class XMLLinkClass extends StaticFormatLinkClass {

    public final static XMLLinkClass instance = new XMLLinkClass();

    @Override
    public String getCaption() {
        return "XML link";
    }

    @Override
    public String getName() {
        return "XMLLINK";
    }
}