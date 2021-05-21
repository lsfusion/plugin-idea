package com.lsfusion.lang.classes;

public class ZDateTimeIntervalClass extends DateTimeIntervalClass {

    public final static DateTimeIntervalClass instance = new DateTimeIntervalClass();

    @Override
    public String getName() {
        return "INTERVAL[ZDATETIME]";
    }

    @Override
    public String getCaption() {
        return "Zoned Datetime Interval";
    }

}
