package com.lsfusion.refactoring;

public class CanonicalNameUtils {
    public static final char DELIMITER = '.';

    public static String createCanonicalName(String namespace, String name) {
        return namespace + DELIMITER + name;
    }

    public static String toSID(String canonicalName) {
        return canonicalName.replace(DELIMITER, '_');
    }

    public static String getNamespace(String canonicalName) {
        return canonicalName.substring(0, delimiterPosition(canonicalName));
    }

    public static String getName(String canonicalName) {
        return canonicalName.substring(delimiterPosition(canonicalName) + 1);
    }

    private static int delimiterPosition(String canonicalName) {
        return canonicalName.indexOf(DELIMITER);
    }

    public static boolean isCorrect(String canonicalName) {
        return delimiterPosition(canonicalName) > 0
                && isCorrectSimpleName(getNamespace(canonicalName))
                && isCorrectSimpleName(getName(canonicalName));
    }

    private static boolean isCorrectSimpleName(String name) {
        return name.matches("[a-zA-Z0-9_]+");
    }
}
