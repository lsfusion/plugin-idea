package com.lsfusion.lang.classes.link;

import java.awt.*;

public class DynamicFormatLinkClass extends LinkClass {

    public final static DynamicFormatLinkClass instance = new DynamicFormatLinkClass();

    @Override
    public String getCaption() {
        return "Ссылка на файл";
    }

    @Override
    public String getName() {
        return "CUSTOMLINK";
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