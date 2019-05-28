package com.lsfusion.lang.psi.declarations.policies;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.refactoring.PropertyCanonicalNameUtils;

import java.util.List;

public class FixedSizeUnderscoreDBNamingPolicy implements DBNamingPolicy {
    private final int maxIDLength;
    private final String autoTablesPrefix;

    public FixedSizeUnderscoreDBNamingPolicy(int maxIDLength, String autoTablesPrefix) {
        this.maxIDLength = maxIDLength;
        this.autoTablesPrefix = autoTablesPrefix;
    }

    @Override
    public String createAutoTableDBName(List<LSFClassSet> classes) {
        StringBuilder builder = new StringBuilder(autoTablesPrefix);
        for (LSFClassSet valueClass : classes) {
            builder.append('_');
            builder.append(valueClass.getCanonicalName());
        }
        return cutToMaxLength(builder.toString());
    }

    // Заменяет знаки '?' на 'null', затем все спец символы заменяет на '_', и удаляет подчеркивания в конце
    @Override
    public String transformActionOrPropertyCNToDBName(String canonicalName) {
        String dbName = replaceUnknownClassesWithNull(canonicalName);
        dbName = replaceAllNonIDLettersWithUnderscore(dbName);
        dbName = removeTrailingUnderscores(dbName);
        return cutToMaxLength(dbName);
    }
 
    private String removeTrailingUnderscores(String name) {
        return name.replaceAll("_+$", "");
    }
    
    private String replaceAllNonIDLettersWithUnderscore(String name) {
        return name.replaceAll("[^a-zA-Z0-9_]", "_");
    }
    
    private String replaceUnknownClassesWithNull(String name) {
        return name.replace(PropertyCanonicalNameUtils.UNKNOWNCLASS, "null");
    }

    private static final char DELIMITER = '.';
    @Override
    public String transformTableCNToDBName(String canonicalName) {
        return cutToMaxLength(canonicalName.replace(DELIMITER, '_'));
    }

    String cutToMaxLength(String s) {
        if (s.length() > maxIDLength) {
            s = s.substring(0, maxIDLength);
        }
        return s;
    }

    @Override
    public String getAutoTablesPrefix() {
        return autoTablesPrefix;
    }
}
