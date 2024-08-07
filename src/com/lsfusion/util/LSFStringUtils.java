package com.lsfusion.util;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static com.lsfusion.util.LSFStringUtils.StringSpecialBlockType.INTERPOLATION;
import static com.lsfusion.util.LSFStringUtils.StringSpecialBlockType.LOCALIZATION;

public class LSFStringUtils {
    private static final String specialEscapeCharacters = "nrt";
    public static final char QUOTE = '\'';

    public static final char INTERP_CH = '$';
    public static final char INLINE_CH = 'I';
    public static final char RESOURCE_CH = 'R';

    public static final String INTERPOLATION_PREFIX = "${";

    // removes quotes, removes escaping, transforms special \n \r \t sequences to special characters (optional)
    public static String getSimpleLiteralValue(String literal, String unescapeCharacters, boolean transformSpecialSequences) {
        StringBuilder builder = new StringBuilder();
        for (int i = 1; i+1 < literal.length(); ++i) {
            char cur = literal.charAt(i);
            if (cur == '\\' && i+1 < literal.length()) {
                char next = literal.charAt(i+1);
                if (transformSpecialSequences && specialEscapeCharacters.indexOf(next) != -1) {
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
        return getSimpleLiteralValue(literal, unescapeCharacters, false);
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

    // Takes into account that backslash may be escaped itself
    public static String unescapeQuotes(@NotNull String s) {
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < s.length(); ++i) {
            if (s.charAt(i) == '\\' && i+1 < s.length()) {
                if (s.charAt(i+1) != QUOTE) {
                    result.append('\\');
                }
                ++i;
            }
            result.append(s.charAt(i));
        }
        return result.toString();
    }
    
    public enum StringSpecialBlockType { NONE, LOCALIZATION, INTERPOLATION, INLINE, RESOURCE }

    public static class SpecialBlock {
        public int start, end;
        public StringSpecialBlockType type;

        public SpecialBlock(int start, int end, StringSpecialBlockType type) {
            this.start = start;
            this.end = end;
            this.type = type;
        }
    }

    /**
     * @param literal   literal with quotes
     */
    public static List<SpecialBlock> specialBlockList(@NotNull String literal, boolean isExpressionString) {
        List<SpecialBlock> blocks = new ArrayList<>();
        int pos = 0;
        int nestingDepth = 0;
        int blockStartPos = 0;
        StringSpecialBlockType state = StringSpecialBlockType.NONE;
        while (pos < literal.length()) {
            char c = literal.charAt(pos);
            if (c == '\\') {
                ++pos;
            } else if (nestingDepth > 0) {
                if (c == '{') {
                    ++nestingDepth;
                } else if (c == '}') {
                    --nestingDepth;
                    if (nestingDepth == 0) {
                        blocks.add(new SpecialBlock(blockStartPos, pos, state));
                        state = StringSpecialBlockType.NONE;
                    }
                }
            } else {
                StringSpecialBlockType type = positionType(literal, pos, isExpressionString);
                if (type != StringSpecialBlockType.NONE) {
                    nestingDepth = 1;
                    blockStartPos = pos;
                    pos += additionalShift(type);
                    state = type;
                }
            }
            ++pos;
        }
        return blocks;
    }

    private static StringSpecialBlockType positionType(String s, int pos, boolean isExpressionString) {
        if (compareChar(s, pos, '{')) return LOCALIZATION;
        if (compareChar(s, pos, INTERP_CH) && isExpressionString) {
            if (compareChar(s, pos + 1, '{')) return StringSpecialBlockType.INTERPOLATION;
            if (compareChar(s, pos + 1, INLINE_CH) && compareChar(s, pos + 2, '{'))
                return StringSpecialBlockType.INLINE;
            if (compareChar(s, pos + 1, RESOURCE_CH) && compareChar(s, pos + 2, '{'))
                return StringSpecialBlockType.RESOURCE;
        }
        return StringSpecialBlockType.NONE;
    }

    private static int additionalShift(StringSpecialBlockType type) {
        switch (type) {
            case NONE:
            case LOCALIZATION:
                return 0;
            case INTERPOLATION:
                return 1;
            case INLINE:
            case RESOURCE:
                return 2;
        }
        return 0;
    }

    private static boolean compareChar(String source, int pos, char cmp) {
        return pos < source.length() && source.charAt(pos) == cmp;
    }

    public static List<SpecialBlock> specialBlockList(@NotNull String literal, boolean isExpressionString, StringSpecialBlockType type) {
        return specialBlockList(literal, isExpressionString)
                .stream()
                .filter(block -> block.type == type)
                .collect(Collectors.toList());
    }

    public static List<SpecialBlock> getInterpolationBlockList(@NotNull String literal, boolean isExpressionString) {
        return specialBlockList(literal, isExpressionString, INTERPOLATION);
    }

    public static boolean hasSpecialBlock(@NotNull String literal, boolean isExpressionString) {
        return !specialBlockList(literal, isExpressionString).isEmpty();
    }

    public static boolean hasLocalizationBlock(@NotNull String literal, boolean isExpressionString) {
        return hasBlock(literal, isExpressionString, LOCALIZATION);
    }

    public static boolean hasInterpolationBlock(@NotNull String literal, boolean isExpressionString) {
        return hasBlock(literal, isExpressionString, INTERPOLATION);
    }

    private static boolean hasBlock(@NotNull String literal, boolean isExpressionString, final StringSpecialBlockType type) {
        return specialBlockList(literal, isExpressionString).stream().anyMatch(block -> block.type == type);
    }
    
    public static boolean isRawLiteral(@NotNull String literal) {
        if (!(literal.startsWith("r") || literal.startsWith("R"))) return false;
        int len = literal.length();
        if (len > 2 && literal.charAt(1) == QUOTE && literal.charAt(len-1) == QUOTE)
            return true;
        return len > 4
                && literal.charAt(2) == QUOTE
                && literal.charAt(len-2) == QUOTE
                && literal.charAt(1) == literal.charAt(len-1);
    }
    
    public static String getRawLiteralValue(@NotNull String literal) {
        assert isRawLiteral(literal);
        return getSimpleLiteralValue(removeRawLiteralSyntax(literal), "", false);
    }
    
    // returns "simple" literal
    // r'text' -> 'text'
    // r$'text'$ -> 'text'
    private static String removeRawLiteralSyntax(@NotNull String literal) {
        assert isRawLiteral(literal);
        if (literal.charAt(1) != QUOTE) {
            return literal.substring(2, literal.length() - 1);
        } else {
            return literal.substring(1);
        }
    }
}
