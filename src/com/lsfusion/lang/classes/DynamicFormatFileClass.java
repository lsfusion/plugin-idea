package com.lsfusion.lang.classes;

public class DynamicFormatFileClass extends AbstractDynamicFormatFileClass {

    public final static DynamicFormatFileClass instance = new DynamicFormatFileClass();

    @Override
    public String getCaption() {
        return "File";
    }

    @Override
    public String getName() {
        return "FILE";
    }
}
