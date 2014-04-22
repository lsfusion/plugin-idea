package com.lsfusion.lang.classes;

import java.awt.*;

public class WordClass extends StaticFormatFileClass {

    public final static WordClass instance = new WordClass();

    @Override
    public String getCaption() {
        return "Файл Word";
    }

    @Override
    public String getName() {
        return "WORDFILE";
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
