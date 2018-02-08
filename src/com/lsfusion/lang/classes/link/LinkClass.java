package com.lsfusion.lang.classes.link;

import com.lsfusion.lang.classes.DataClass;

import java.awt.*;

public abstract class LinkClass extends DataClass {
    @Override
    public String getMask() {
        return "1234567";
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