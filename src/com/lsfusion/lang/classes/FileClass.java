package com.lsfusion.lang.classes;

import java.awt.*;

public abstract class FileClass extends DataClass {
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
