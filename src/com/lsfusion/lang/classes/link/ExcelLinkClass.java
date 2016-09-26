package com.lsfusion.lang.classes.link;

import java.awt.*;

public class ExcelLinkClass extends StaticFormatLinkClass {

    public final static ExcelLinkClass instance = new ExcelLinkClass();

    @Override
    public String getCaption() {
        return "Ссылка на файл Excel";
    }

    @Override
    public String getName() {
        return "EXCELLINK";
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