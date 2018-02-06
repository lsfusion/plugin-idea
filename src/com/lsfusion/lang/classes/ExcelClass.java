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

    @Override
    public int getHeight(FontMetrics font) {
        return 18;
    }

    @Override
    public int getWidth(int minCharWidth, FontMetrics font) {
        return 18;
    }
}
