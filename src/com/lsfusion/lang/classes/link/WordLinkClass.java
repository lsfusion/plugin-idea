package com.lsfusion.lang.classes.link;

import java.awt.*;

public class WordLinkClass extends StaticFormatLinkClass {

    public final static WordLinkClass instance = new WordLinkClass();

    @Override
    public String getCaption() {
        return "Ссылка на файл Word";
    }

    @Override
    public String getName() {
        return "WORDLINK";
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