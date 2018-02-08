package com.lsfusion.lang.classes;

import java.awt.*;

public class ColorClass extends DataClass {

    public final static ColorClass instance = new ColorClass();

    @Override
    public String getName() {
        return "COLOR";
    }

    @Override
    public String getCaption() {
        return "Цвет";
    }

    @Override
    public String getMask() {
        throw new UnsupportedOperationException();
    }

    @Override
    public int getWidth(int minCharWidth, FontMetrics fontMetrics) {
        return 40;
    }
}
