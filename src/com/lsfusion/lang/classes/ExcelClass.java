package com.lsfusion.lang.classes;

import java.awt.*;

public class ExcelClass extends StaticFormatFileClass {

    public final static ExcelClass instance = new ExcelClass();

    @Override
    public String getCaption() {
        return "Файл Excel";
    }

    @Override
    public String getName() {
        return "EXCELFILE";
    }
}
