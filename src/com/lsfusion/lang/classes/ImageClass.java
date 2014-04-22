package com.lsfusion.lang.classes;

import java.awt.*;

public class ImageClass extends StaticFormatFileClass {

    public final static ImageClass instance = new ImageClass();

    @Override
    public String getCaption() {
        return "Изображение";
    }

    @Override
    public String getName() {
        return "IMAGEFILE";
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
