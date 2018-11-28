package com.lsfusion.lang.classes.link;

public class TableLinkClass extends StaticFormatLinkClass {

    public final static TableLinkClass instance = new TableLinkClass();

    @Override
    public String getCaption() {
        return "Ссылка на файл таблицы";
    }

    @Override
    public String getName() {
        return "TABLELINK";
    }
}
