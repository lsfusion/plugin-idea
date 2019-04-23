package com.lsfusion.lang.classes.link;

public class HTMLLinkClass extends StaticFormatLinkClass {

    public final static HTMLLinkClass instance = new HTMLLinkClass();

    @Override
    public String getCaption() {
        return "HTML link";
    }

    @Override
    public String getName() {
        return "HTMLLINK";
    }
}