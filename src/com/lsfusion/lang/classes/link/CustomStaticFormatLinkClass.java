package com.lsfusion.lang.classes.link;

public class CustomStaticFormatLinkClass extends StaticFormatLinkClass {
    @Override
    public String getCaption() {
        return "Ссылка на файл";
    }

    @Override
    public String getName() {
        return "CustomLink";
    }
}