package com.lsfusion.lang.classes;

public class CSVClass extends StaticFormatFileClass {

    public final static CSVClass instance = new CSVClass();

    @Override
    public String getCaption() {
        return "Файл CSV";
    }

    @Override
    public String getName() {
        return "CSVFILE";
    }
}
