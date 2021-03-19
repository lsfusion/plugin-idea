package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

import java.text.Format;
import java.text.SimpleDateFormat;

public class DateIntervalClass extends FormatClass {

    public final static DateIntervalClass instance = new DateIntervalClass();

    @Override
    public String getName() {
        return "INTERVAL[DATE]";
    }

    @Override
    public String getCaption() {
        return "Date Interval";
    }

    @Override
    public Format getDefaultFormat() {
        return DateUtils.dateFormat;
    }

    @Override
    public Format createUserFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    protected int getDefaultCharWidth() {
        return DateUtils.wideFormattableDate.toString().length();
    }
}
