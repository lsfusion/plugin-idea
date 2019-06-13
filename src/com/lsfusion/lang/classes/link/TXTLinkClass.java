package com.lsfusion.lang.classes.link;

public class TXTLinkClass extends StaticFormatLinkClass {

    public final static TXTLinkClass instance = new TXTLinkClass();

    @Override
    public String getCaption() {
        return "Text link";
    }

    @Override
    public String getName() {
        return "TEXTLINK";
    }
}