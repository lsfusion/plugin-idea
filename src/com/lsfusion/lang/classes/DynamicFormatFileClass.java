package com.lsfusion.lang.classes;

import java.awt.*;

public class DynamicFormatFileClass extends FileClass {

    public final static DynamicFormatFileClass instance = new DynamicFormatFileClass();

    @Override
    public String getCaption() {
        return "Файл";
    }

    @Override
    public String getName() {
        return "CUSTOMFILE";
    }
}
