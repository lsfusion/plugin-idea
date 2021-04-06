package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

import java.text.Format;
import java.text.SimpleDateFormat;

public class DateTimeIntervalClass extends FormatClass {

    public final static DateTimeIntervalClass instance = new DateTimeIntervalClass();

    @Override
    public String getName() {
        return "INTERVAL[DATETIME]";
    }

    @Override
    public String getCaption() {
        return "Datetime Interval";
    }

    @Override
    public Format getDefaultFormat() {
        return DateUtils.dateTimeFormat;
    }

    @Override
    public Format createUserFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    protected int getDefaultCharWidth() {
        return DateUtils.wideFormattableDateTime.toString().length();
    }
}
