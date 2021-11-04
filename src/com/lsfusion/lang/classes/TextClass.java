package com.lsfusion.lang.classes;

import java.awt.*;

public class TextClass extends StringClass {

    private final String type;

    public TextClass() {
        this(null);
    }

    public TextClass(String type) {
        super(false, false, ExtInt.UNLIMITED);
        this.type = type;
    }

    public DataClass op(DataClass compClass, boolean or, boolean string) {
        return compClass instanceof StringClass ? this : super.op(compClass, or, string);
    }

    public String getName() {
        return "TEXT";
    }

    @Override
    public String getCaption() {
        return "Text" + (type != null ? " (" + type + ")" : "");
    }

    @Override
    public int getDefaultHeight(FontMetrics fontMetrics, int charHeight) {
        return super.getDefaultHeight(fontMetrics, charHeight == 1 && length.isUnlimited() ? 4 : charHeight);
    }

    @Override
    public String toString() {
        return "TEXT";
    }
}
