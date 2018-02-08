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
}
