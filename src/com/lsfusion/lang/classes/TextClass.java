package com.lsfusion.lang.classes;

import com.lsfusion.util.BaseUtils;

import java.awt.*;

public class TextClass extends StringClass {

    public static final TextClass instance = new TextClass(false);
    public static final TextClass richInstance = new TextClass(true);

    public final boolean rich;

    public TextClass(boolean rich) {
        super(false, false, ExtInt.UNLIMITED);
        this.rich = rich;
    }

    public DataClass op(DataClass compClass, boolean or, boolean string) {
        if(compClass instanceof StringClass)
            return BaseUtils.cmp(rich, compClass instanceof TextClass && ((TextClass) compClass).rich, or) ? richInstance : instance;

        return super.op(compClass, or, string);
    }

    public String getName() {
        return rich ? "RICHTEXT" : "TEXT";
    }

    @Override
    public String getCaption() {
        return "Text" + (rich ? " (rich)" : "");
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
