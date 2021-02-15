package com.lsfusion.lang.classes;

import com.lsfusion.util.DateUtils;

import java.text.DateFormat;
import java.text.Format;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import static com.lsfusion.util.DateUtils.wideFormattableDateTime;

public class TimeClass extends FormatClass {

    public final static TimeClass instance = new TimeClass();

    @Override
    public String getName() {
        return "TIME";
    }

    @Override
    public String getCaption() {
        return "Time";
    }

    @Override
    protected SimpleDateFormat getEditFormat(Format format) {
        return DateUtils.createTimeEditFormat((DateFormat)format);
    }

    @Override
    public SimpleDateFormat createUserFormat(String pattern) {
        return new SimpleDateFormat(pattern);
    }

    @Override
    protected Object getDefaultWidthValue() {
        return wideFormattableDateTime;
    }

    @Override
    public Format getDefaultFormat() {
        return DateUtils.timeFormat;
    }

    public String formatString(Object obj) throws ParseException {
        if (obj != null) {
            return DateUtils.timeFormat.format(obj);
        } else return "";
    }

    @Override
    public ExtInt getCharLength() {
        return new ExtInt(25);
    }
}
