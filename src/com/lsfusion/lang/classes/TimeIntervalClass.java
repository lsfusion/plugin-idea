package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

import java.text.Format;
import java.text.SimpleDateFormat;

public class TimeIntervalClass extends FormatClass {

    public final static TimeIntervalClass instance = new TimeIntervalClass();

    @Override
    public String getName() {
        return "INTERVAL[TIME]";
    }

    @Override
    public String getCaption() {
        return "Time Interval";
    }

    @Override
    public Format getDefaultFormat() {
        return DateUtils.timeFormat;
    }

    @Override
    public Format createUserFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    protected int getDefaultCharWidth() {
        return DateUtils.wideFormattableDateTime.toString().length();
    }
}
