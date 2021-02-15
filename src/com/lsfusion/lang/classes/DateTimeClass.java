package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

import java.awt.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;

import static com.lsfusion.util.DateUtils.wideFormattableDateTime;

public class DateTimeClass extends FormatClass {

    public final static DateTimeClass instance = new DateTimeClass();

    @Override
    public SimpleDateFormat createUserFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

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
        return "Datetime";
    }

    @Override
    protected Object getDefaultWidthValue() {
        return wideFormattableDateTime;
    }

    @Override
    public Format getDefaultFormat() {
        return DateUtils.dateTimeFormat;
    }

    @Override
    public int getFullWidthString(String widthString, FontMetrics fontMetrics) {
        return super.getFullWidthString(widthString, fontMetrics) + 21;
    }

    @Override
    protected SimpleDateFormat getEditFormat(Format format) {
        return DateUtils.createDateTimeEditFormat((DateFormat) format);
    }

    @Override
    public ExtInt getCharLength() {
        return new ExtInt(25);
    }
}
