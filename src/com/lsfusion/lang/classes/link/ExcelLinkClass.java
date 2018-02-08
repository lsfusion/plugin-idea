package com.lsfusion.lang.classes.link;

import java.awt.*;

public class ExcelLinkClass extends StaticFormatLinkClass {

    public final static ExcelLinkClass instance = new ExcelLinkClass();

    @Override
    public String getCaption() {
        return "Ссылка на файл Excel";
    }

    @Override
    public String getName() {
        return "EXCELLINK";
    }
}