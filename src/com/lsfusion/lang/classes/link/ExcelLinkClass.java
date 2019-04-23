package com.lsfusion.lang.classes.link;

public class ExcelLinkClass extends StaticFormatLinkClass {

    public final static ExcelLinkClass instance = new ExcelLinkClass();

    @Override
    public String getCaption() {
        return "Excel link";
    }

    @Override
    public String getName() {
        return "EXCELLINK";
    }
}