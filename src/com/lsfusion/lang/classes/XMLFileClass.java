package com.lsfusion.lang.classes;

public class XMLFileClass extends StaticFormatFileClass {

    public final static XMLFileClass instance = new XMLFileClass();

    @Override
    public String getCaption() {
        return "XML file";
    }

    @Override
    public String getName() {
        return "XMLFILE";
    }
}
