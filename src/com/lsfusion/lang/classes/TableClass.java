package com.lsfusion.lang.classes;

public class TableClass extends StaticFormatFileClass {

    public final static TableClass instance = new TableClass();

    @Override
    public String getCaption() {
        return "Table file";
    }

    @Override
    public String getName() {
        return "TABLEFILE";
    }
}
