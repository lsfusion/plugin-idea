package com.lsfusion.lang.classes;

public class PDFClass extends StaticFormatFileClass {

    public final static PDFClass instance = new PDFClass();

    @Override
    public String getCaption() {
        return "PDF file";
    }

    @Override
    public String getName() {
        return "PDFFILE";
    }
}
