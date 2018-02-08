package com.lsfusion.lang.classes.link;

import java.awt.*;

public class DynamicFormatLinkClass extends LinkClass {

    public final static DynamicFormatLinkClass instance = new DynamicFormatLinkClass();

    @Override
    public String getCaption() {
        return "Ссылка на файл";
    }

    @Override
    public String getName() {
        return "CUSTOMLINK";
    }
}