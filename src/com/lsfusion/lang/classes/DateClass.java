package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

public class DateClass extends DataClass {

    public final static DateClass instance = new DateClass();

    @Override
    public String getName() {
        return "DATE";
    }

    @Override
    public String getCaption() {
        return "Дата";
    }

    @Override
    public String getPreferredMask() {
        return DateUtils.dateEditFormat.format(DateUtils.wideFormattableDate) + "BTN";
    }

    @Override
    public boolean isFlex() {
        return false;
    }
}
