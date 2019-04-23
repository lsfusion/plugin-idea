package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

import java.awt.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;

public class DateClass extends FormatClass {

    @Override
    protected SimpleDateFormat getEditFormat(Format format) {
        return DateUtils.createDateEditFormat((DateFormat) format);
    }

    @Override
    protected Object getDefaultWidthValue() {
        return DateUtils.wideFormattableDate;
    }

    @Override
    public SimpleDateFormat createUserFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    public Format getDefaultFormat() {
        return DateUtils.dateFormat;
    }

    public final static DateClass instance = new DateClass();

    @Override
    public String getName() {
        return "DATE";
    }

    @Override
    public String getCaption() {
        return "Date";
    }

    @Override
    public int getFullWidthString(String widthString, FontMetrics fontMetrics) {
        return super.getFullWidthString(widthString, fontMetrics) + 21;
    }
}
