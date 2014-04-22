package com.lsfusion.lang.classes;

import java.awt.*;

public class DynamicFormatFileClass extends FileClass {

    public final static DynamicFormatFileClass instance = new DynamicFormatFileClass();

    @Override
    public String getCaption() {
        return "Файл";
    }

    @Override
    public String getName() {
        return "CUSTOMFILE";
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
