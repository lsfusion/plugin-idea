package com.lsfusion.lang.classes;

public class ExcelClass extends StaticFormatFileClass {

    public final static ExcelClass instance = new ExcelClass();

    @Override
    public String getCaption() {
        return "Excel file";
    }

    @Override
    public String getName() {
        return "EXCELFILE";
    }
}
