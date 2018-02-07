package com.lsfusion.lang.classes;

import java.awt.*;

public class ObjectClass extends DataClass {
    @Override
    public String getCaption() {
        return "Объект";
    }

    @Override
    public String getName() {
        return "OBJECT";
    }

    @Override
    public int getWidth(int minCharWidth, FontMetrics fontMetrics) {
        return fontMetrics.stringWidth("999 999") + 8;
    }

    @Override
    public int getHeight(FontMetrics fontMetrics) {
        return fontMetrics.getHeight() + 1;
    }

    @Override
    public String getMask() {
        return "1234";
    }
}
