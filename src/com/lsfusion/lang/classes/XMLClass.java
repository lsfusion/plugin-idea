package com.lsfusion.lang.classes;

public class XMLClass extends StaticFormatFileClass {

    public final static XMLClass instance = new XMLClass();

    @Override
    public String getCaption() {
        return "XML file";
    }

    @Override
    public String getName() {
        return "XMLFILE";
    }
}
