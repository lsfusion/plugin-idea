package com.lsfusion.lang.classes.link;

public class PDFLinkClass extends StaticFormatLinkClass {

    public final static PDFLinkClass instance = new PDFLinkClass();

    @Override
    public String getCaption() {
        return "PDF link";
    }

    @Override
    public String getName() {
        return "PDFLINK";
    }
}