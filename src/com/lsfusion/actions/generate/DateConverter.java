package com.lsfusion.actions.generate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

//based on DateConverter and TimeConverter from server
public class DateConverter {

    private static final Map<String, String> DATE_FORMAT_REGEXPS = new HashMap<>();
    private static final Map<String, String> TIME_FORMAT_REGEXPS = new HashMap<>();
    private static final Map<String, String> DATETIME_FORMAT_REGEXPS = new HashMap<>();
    private static final Map<String, String> ZONED_DATETIME_FORMAT_REGEXPS = new HashMap<>();

    static {
        DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}$", "dd.MM.yy");
        DATE_FORMAT_REGEXPS.put("^\\d{8}$", "yyyyMMdd");
        DATE_FORMAT_REGEXPS.put("^\\d{1,2}-\\d{1,2}-\\d{4}$", "dd-MM-yyyy");
        DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}$", "dd.MM.yyyy");
        DATE_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}$", "yyyy-MM-dd");
        DATE_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}[+-]\\d{2}:\\d{2}$", "yyyy-MM-ddXXX");
        DATE_FORMAT_REGEXPS.put("^\\d{1,2}/\\d{1,2}/\\d{4}$", "MM/dd/yyyy");
        DATE_FORMAT_REGEXPS.put("^\\d{4}/\\d{1,2}/\\d{1,2}$", "yyyy/MM/dd");
        DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}$", "dd MMM yyyy");
        DATE_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}$", "dd MMMM yyyy");
    }

    static {
        TIME_FORMAT_REGEXPS.put("^\\d{1,2}:\\d{1,2}:\\d{1,2}$", "HH:mm:ss");
        TIME_FORMAT_REGEXPS.put("^\\d{1,2}:\\d{1,2}$", "HH:mm");
    }

    static {
        DATETIME_FORMAT_REGEXPS.put("^\\d{12}$", "yyyyMMddHHmm");
        DATETIME_FORMAT_REGEXPS.put("^\\d{8}\\s\\d{4}$", "yyyyMMdd HHmm");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}$", "dd-MM-yyyy HH:mm");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}:\\d{2}$", "dd.MM.yy HH:mm");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}:\\d{2}$", "dd.MM.yyyy HH:mm");
        DATETIME_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy-MM-dd HH:mm");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}$", "MM/dd/yyyy HH:mm");
        DATETIME_FORMAT_REGEXPS.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}$", "yyyy/MM/dd HH:mm");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMM yyyy HH:mm");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}$", "dd MMMM yyyy HH:mm");
        DATETIME_FORMAT_REGEXPS.put("^\\d{14}$", "yyyyMMddHHmmss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{8}\\s\\d{6}$", "yyyyMMdd HHmmss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}-\\d{1,2}-\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}-\\d{1,2}-\\d{4}t\\d{1,2}:\\d{2}:\\d{2}$", "dd-MM-yyyy'T'HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\.\\d{1,2}\\.\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd.MM.yy HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd.MM.yyyy HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\.\\d{1,2}\\.\\d{4}t\\d{1,2}:\\d{2}:\\d{2}$", "dd.MM.yyyy'T'HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}t\\d{1,2}:\\d{2}:\\d{2}$", "yyyy-MM-dd'T'HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}/\\d{1,2}/\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}/\\d{1,2}/\\d{4}t\\d{1,2}:\\d{2}:\\d{2}$", "MM/dd/yyyy'T'HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{4}/\\d{1,2}/\\d{1,2}\\s\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{4}/\\d{1,2}/\\d{1,2}t\\d{1,2}:\\d{2}:\\d{2}$", "yyyy/MM/dd'T'HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{3}\\s\\d{4}t\\d{1,2}:\\d{2}:\\d{2}$", "dd MMM yyyy'T'HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}\\s\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{1,2}\\s[a-z]{4,}\\s\\d{4}t\\d{1,2}:\\d{2}:\\d{2}$", "dd MMMM yyyy'T'HH:mm:ss");
        DATETIME_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}t\\d{1,2}:\\d{2}:\\d{2}\\.\\d{3}$", "yyyy-MM-dd'T'HH:mm:ss.SSS");
    }
    private static final String DATE_SYMBOLS_REGEXP = "[.-/:]";

    static {
        ZONED_DATETIME_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}t\\d{1,2}:\\d{2}:\\d{2}[+-]\\d{2}:\\d{2}$", "yyyy-MM-dd'T'HH:mm:ssXXX");
        ZONED_DATETIME_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}t\\d{1,2}:\\d{2}:\\d{2}\\.\\d[+-]\\d{2}:\\d{2}$", "yyyy-MM-dd'T'HH:mm:ss.SXXX");
        ZONED_DATETIME_FORMAT_REGEXPS.put("^\\d{4}-\\d{1,2}-\\d{1,2}t\\d{1,2}:\\d{2}:\\d{2}\\.\\d{2,3}(([+-]\\d{2}:\\d{2})|z)$", "yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    }

    public static String smartParse(String dateString) {
        dateString = dateString.trim();
        if(dateString.isEmpty())
            return null;            
            
        for (String regexp : DATE_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                LocalDate.parse(dateString, DateTimeFormatter.ofPattern(DATE_FORMAT_REGEXPS.get(regexp)));
                return "DATE";
            }
        }


        for (String regexp : TIME_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                LocalTime.parse(dateString, DateTimeFormatter.ofPattern(TIME_FORMAT_REGEXPS.get(regexp)));
                return "TIME";
            }
        }

        for (String regexp : DATETIME_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                LocalDateTime.parse(dateString, DateTimeFormatter.ofPattern(DATETIME_FORMAT_REGEXPS.get(regexp)));
                return "DATETIME";
            }
        }

        for (String regexp : ZONED_DATETIME_FORMAT_REGEXPS.keySet()) {
            if (dateString.toLowerCase().matches(regexp)) {
                ZonedDateTime.parse(dateString, DateTimeFormatter.ofPattern(ZONED_DATETIME_FORMAT_REGEXPS.get(regexp)));
                return "ZDATETIME";
            }
        }
    
        return null;
    }
}
