package com.lsfusion.util;

public class LSFStringUtils {
    private static String specialEscapeCharacters = "nrt";

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

    // removes quotes, removes escaping and DON'T transform special \n \r \t sequences to special characters
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
}
