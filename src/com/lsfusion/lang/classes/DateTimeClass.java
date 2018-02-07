package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

import java.util.Collection;
import java.util.Collections;

public class DateTimeClass extends DataClass {

    public final static DateTimeClass instance = new DateTimeClass();

    @Override
    public Collection<String> getExtraNames() {
        return Collections.singletonList("DateTime");
    }

    @Override
    public String getName() {
        return "DATETIME";
    }

    @Override
    public String getCaption() {
        return "Дата со временем";
    }

    @Override
    public String getMask() {
        return DateUtils.dateTimeEditFormat.format(DateUtils.wideFormattableDateTime) + "BTN";
    }
}
