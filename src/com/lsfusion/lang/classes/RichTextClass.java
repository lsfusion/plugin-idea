package com.lsfusion.lang.classes;

public class RichTextClass extends TextClass {

    public RichTextClass() {
        super("rich");
    }

    public String getName() {
        return "RICHTEXT";
    }

    @Override
    public String toString() {
        return "RICHTEXT";
    }

    public DataClass op(DataClass compClass, boolean or, boolean string) {
        return compClass instanceof RichTextClass ? this : super.op(compClass, or, string);
    }
}
