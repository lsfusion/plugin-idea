package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

public class DateTimeClass extends DataClass {

    public final static DateTimeClass instance = new DateTimeClass();

    @Override
    public String getName() {
        return "DATETIME";
    }

    @Override
    public String getCaption() {
        return "Дата со временем";
    }

    @Override
    public String getPreferredMask() {
        return DateUtils.dateTimeEditFormat.format(DateUtils.wideFormattableDateTime) + "BTN";
    }
}
