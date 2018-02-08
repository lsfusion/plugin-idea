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
}