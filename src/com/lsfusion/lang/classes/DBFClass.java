package com.lsfusion.lang.classes;

public class DBFClass extends StaticFormatFileClass {

    public final static DBFClass instance = new DBFClass();

    @Override
    public String getCaption() {
        return "DBF file";
    }

    @Override
    public String getName() {
        return "DBFFILE";
    }
}
