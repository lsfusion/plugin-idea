package com.lsfusion.lang.classes;

public class WordClass extends StaticFormatFileClass {

    public final static WordClass instance = new WordClass();

    @Override
    public String getCaption() {
        return "Word file";
    }

    @Override
    public String getName() {
        return "WORDFILE";
    }
}
