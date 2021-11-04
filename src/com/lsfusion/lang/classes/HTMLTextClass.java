package com.lsfusion.lang.classes;

public class HTMLTextClass extends TextClass {

    public HTMLTextClass() {
        super("html");
    }

    public String getName() {
        return "HTMLTEXT";
    }

    @Override
    public String toString() {
        return "HTMLTEXT";
    }

    public DataClass op(DataClass compClass, boolean or, boolean string) {
        return compClass instanceof HTMLTextClass ? this : super.op(compClass, or, string);
    }
}
