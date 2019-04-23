package com.lsfusion.lang.classes.link;

public class DynamicFormatLinkClass extends LinkClass {

    public final static DynamicFormatLinkClass instance = new DynamicFormatLinkClass();

    @Override
    public String getCaption() {
        return "File link";
    }

    @Override
    public String getName() {
        return "LINK";
    }
}