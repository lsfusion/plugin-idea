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
}
