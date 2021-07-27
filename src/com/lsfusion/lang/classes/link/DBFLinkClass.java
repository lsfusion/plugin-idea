package com.lsfusion.lang.classes.link;

public class DBFLinkClass extends StaticFormatLinkClass {

    public final static DBFLinkClass instance = new DBFLinkClass();

    @Override
    public String getCaption() {
        return "DBF link";
    }

    @Override
    public String getName() {
        return "DBFLINK";
    }
}