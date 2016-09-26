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

    @Override
    public int getPreferredHeight(FontMetrics font) {
        return 18;
    }

    @Override
    public int getPreferredWidth(int prefCharWidth, FontMetrics font) {
        return 18;
    }

    @Override
    public int getMinimumWidth(int minCharWidth, FontMetrics font) {
        return 15;
    }
}