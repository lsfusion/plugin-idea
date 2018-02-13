package com.lsfusion.lang.classes;

import java.text.NumberFormat;

public abstract class IntClass extends IntegralClass {

    @Override
    public NumberFormat getDefaultFormat() {
        NumberFormat format = super.getDefaultFormat();
        format.setParseIntegerOnly(true);
        return format;
    }


    public IntClass() {
    }
}
