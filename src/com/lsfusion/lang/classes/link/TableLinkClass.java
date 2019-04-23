package com.lsfusion.lang.classes.link;

public class TableLinkClass extends StaticFormatLinkClass {

    public final static TableLinkClass instance = new TableLinkClass();

    @Override
    public String getCaption() {
        return "Table file link";
    }

    @Override
    public String getName() {
        return "TABLELINK";
    }
}
