package com.lsfusion.util;

import org.jetbrains.annotations.NotNull;

public class LSFStringUtils {
    private static final String specialEscapeCharacters = "nrt";
    private static final char QUOTE = '\'';

    // removes quotes, removes escaping, transforms special \n \r \t sequences to special characters
    public static String getSimpleLiteralValue(String literal, String unescapeCharacters) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i+1 < literal.length(); ++i) {
            char cur = literal.charAt(i);
            if (cur == '\\' && i+1 < literal.length()) {
                char next = literal.charAt(i+1);
                if (specialEscapeCharacters.indexOf(next) != -1) {
                    builder.append(toSpecialCharacter(next));
                } else if (unescapeCharacters.indexOf(next) != -1) {
                    builder.append(next);
                } else {
                    builder.append(cur);
                    builder.append(next);
                }
                ++i;
            } else {
                builder.append(cur);
            }
        }
        return builder.toString();
    }

    // removes quotes, removes escaping and DO NOT transform special \n \r \t sequences to special characters
    public static String getSimpleLiteralPropertiesFileValue(String literal, String unescapeCharacters) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i+1 < literal.length(); ++i) {
            char cur = literal.charAt(i);
            if (cur == '\\' && i+2 < literal.length() && unescapeCharacters.indexOf(literal.charAt(i+1)) != -1) {
                builder.append(literal.charAt(i+1));
                ++i;
                continue;
            }
            builder.append(cur);
        }
        return builder.toString();
    }

    public static char toSpecialCharacter(char ch) {
        switch (ch) {
            case 'n': return '\n';
            case 'r': return '\r';
            case 't': return '\t';
        }
        return ch;
    }

    public static String quote(@NotNull String s) {
        return QUOTE + s + QUOTE;
    }

    public static String unquote(@NotNull String s) {
        if (isQuoted(s)) {
            return s.substring(1, s.length() - 1);
        }
        return s;
    }

    public static boolean isQuoted(@NotNull String s) {
        return s.length() > 1 && s.charAt(0) == QUOTE && s.charAt(s.length() - 1) == QUOTE;
    }

    public static String escapeQuote(@NotNull String s) {
        return s.replace(String.valueOf(QUOTE), "\\" + QUOTE);
    }
}
