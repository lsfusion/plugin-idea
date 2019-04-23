package com.lsfusion.lang.classes.link;

public class RawLinkClass extends StaticFormatLinkClass {

    public final static RawLinkClass instance = new RawLinkClass();

    @Override
    public String getCaption() {
        return "Raw file link";
    }

    @Override
    public String getName() {
        return "RAWLINK";
    }
}