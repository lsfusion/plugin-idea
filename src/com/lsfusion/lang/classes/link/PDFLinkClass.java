package com.lsfusion.lang.classes.link;

import java.awt.*;

public class PDFLinkClass extends StaticFormatLinkClass {

    public final static PDFLinkClass instance = new PDFLinkClass();

    @Override
    public String getCaption() {
        return "Ссылка на файл PDF";
    }

    @Override
    public String getName() {
        return "PDFLINK";
    }
}