package com.lsfusion.lang.classes.link;

public class RawLinkClass extends StaticFormatLinkClass {

    public final static RawLinkClass instance = new RawLinkClass();

    @Override
    public String getCaption() {
        return "Ссылка на неизвестный файл";
    }

    @Override
    public String getName() {
        return "RAWLINK";
    }
}