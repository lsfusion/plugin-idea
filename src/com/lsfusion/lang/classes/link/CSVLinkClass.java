package com.lsfusion.lang.classes.link;

public class CSVLinkClass extends StaticFormatLinkClass {

    public final static CSVLinkClass instance = new CSVLinkClass();

    @Override
    public String getCaption() {
        return "CSV link";
    }

    @Override
    public String getName() {
        return "CSVLINK";
    }
}