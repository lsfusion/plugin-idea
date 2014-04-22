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
    public String getPreferredMask() {
        return "";
    }

    @Override
    public int getPreferredWidth(int prefCharWidth, FontMetrics fontMetrics) {
        return 50;
    }
}
