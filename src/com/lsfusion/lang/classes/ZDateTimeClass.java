package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

import java.awt.*;
import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Collections;

import static com.lsfusion.util.DateUtils.wideFormattableDateTime;

public class ZDateTimeClass extends FormatClass {

    public final static ZDateTimeClass instance = new ZDateTimeClass();

    @Override
    public SimpleDateFormat createUserFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    @Override
    public Collection<String> getExtraNames() {
        return Collections.singletonList("ZDateTime");
    }

    @Override
    public String getName() {
        return "ZDATETIME";
    }

    @Override
    public String getCaption() {
        return "ZDatetime";
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