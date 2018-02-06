package com.lsfusion.lang.classes;

import java.awt.*;

public class PDFClass extends StaticFormatFileClass {

    public final static PDFClass instance = new PDFClass();

    @Override
    public String getCaption() {
        return "Файл PDF";
    }

    @Override
    public String getName() {
        return "PDFFILE";
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
