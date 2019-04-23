package com.lsfusion.lang.classes.link;

public class WordLinkClass extends StaticFormatLinkClass {

    public final static WordLinkClass instance = new WordLinkClass();

    @Override
    public String getCaption() {
        return "Word link";
    }

    @Override
    public String getName() {
        return "WORDLINK";
    }
}