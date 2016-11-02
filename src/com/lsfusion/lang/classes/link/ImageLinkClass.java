package com.lsfusion.lang.classes.link;

import java.awt.*;

public class ImageLinkClass extends StaticFormatLinkClass {

    public final static ImageLinkClass instance = new ImageLinkClass();

    @Override
    public String getCaption() {
        return "Ссылка на изображение";
    }

    @Override
    public String getName() {
        return "IMAGELINK";
    }

    @Override
    public int getMaximumHeight(FontMetrics fontMetrics) {
        return Integer.MAX_VALUE;
    }

    @Override
    public int getMaximumWidth(int maxCharWidth, FontMetrics fontMetrics) {
        return Integer.MAX_VALUE;
    }
}