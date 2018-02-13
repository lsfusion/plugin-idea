package com.lsfusion.util;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

public class DateUtils {
    public static DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
    public static DateFormat dateTimeFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.MEDIUM);
    public static DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.MEDIUM);
    
    public static Date wideFormattableDate = createWideFormattableDate();
    public static Date wideFormattableDateTime = createWideFormattableDate();

    public static Date createWideFormattableDate() {
        GregorianCalendar gc2 = new GregorianCalendar();
        //просто любая дата, для которой нужны обе цифры при форматтинге
        gc2.set(1991, Calendar.NOVEMBER, 21, 10, 55, 55);
        return gc2.getTime();
    }

    public static SimpleDateFormat createTimeEditFormat(DateFormat dateFormat) {
        if (!(dateFormat instanceof SimpleDateFormat)) {
            //используем паттерн по умолчанию
            return new SimpleDateFormat("HH:mm:ss");
        }
        return createDateEditFormat((SimpleDateFormat) dateFormat);
    }

    public static SimpleDateFormat createDateEditFormat(DateFormat dateFormat) {
        if (!(dateFormat instanceof SimpleDateFormat)) {
            //используем паттерн по умолчанию
            return new SimpleDateFormat("dd.MM.yy");
        }
        return createDateEditFormat((SimpleDateFormat) dateFormat);
    }

    public static SimpleDateFormat createDateTimeEditFormat(DateFormat dateFormat) {
        if (!(dateFormat instanceof SimpleDateFormat)) {
            //используем паттерн по умолчанию
            return new SimpleDateFormat("dd.MM.yy HH:mm:ss");
        }
        return createDateEditFormat((SimpleDateFormat) dateFormat);
    }

    public static SimpleDateFormat createDateEditFormat(SimpleDateFormat simpleFormat) {
        //преобразует данный формат в новый, в котором всем числовым полям даётся максимум места
        //это нужно для того, чтобы можно было создать корректную маску для эдитора

        String doubleSymbols = "GMwdaHhKkms";

        String pattern = simpleFormat.toPattern();
        int patternLength = pattern.length();
        StringBuilder resultPattern = new StringBuilder(patternLength);
        for (int i = 0; i < patternLength; ) {
            char ch = pattern.charAt(i);

            int chCnt = 1;
            while (i + chCnt < patternLength && pattern.charAt(i + chCnt) == ch) ++chCnt;
            i += chCnt;

            if (ch == 'Y' || ch == 'y') {
                if (chCnt > 2) {
                    chCnt = 4;
                } else {
                    chCnt = 2;
                }
            } else if (ch == 'S') {
                chCnt = 3;
            } else if (doubleSymbols.indexOf(ch) != -1) {
                //округляем до верхнего чётного
                chCnt = ((chCnt + 1) >> 1) << 1;
            }
            for (int j = 0; j < chCnt; ++j) {
                resultPattern.append(ch);
            }
        }

        return new SimpleDateFormat(resultPattern.toString());
    }
}
