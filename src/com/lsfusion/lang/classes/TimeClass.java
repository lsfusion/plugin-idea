package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

import java.text.ParseException;

public class TimeClass extends DataClass {

    public final static TimeClass instance = new TimeClass();

    @Override
    public String getName() {
        return "TIME";
    }

    @Override
    public String getCaption() {
        return "Время";
    }

    @Override
    public String getPreferredMask() {
        return DateUtils.timeEditFormat.format(DateUtils.wideFormattableDateTime) + "BT";
    }

    public String formatString(Object obj) throws ParseException {
        if (obj != null) {
            return DateUtils.timeFormat.format(obj);
        } else return "";
    }
}
