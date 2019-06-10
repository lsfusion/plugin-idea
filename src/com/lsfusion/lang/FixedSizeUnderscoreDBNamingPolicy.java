package com.lsfusion.lang;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.refactoring.CanonicalNameUtils;
import com.lsfusion.refactoring.PropertyCanonicalNameUtils;

import java.util.List;

public abstract class FixedSizeUnderscoreDBNamingPolicy extends DBNamingPolicy {
    private final int maxIDLength;
    private final String autoTablesPrefix;

    public FixedSizeUnderscoreDBNamingPolicy(int maxIDLength, String autoTablesPrefix) {
        this.maxIDLength = maxIDLength;
        this.autoTablesPrefix = autoTablesPrefix;
    }

    @Override
    public String createActionOrPropertyDBName(String namespaceName, String name, List<LSFClassSet> signature) {
        String canonicalName = PropertyCanonicalNameUtils.createName(namespaceName, name, signature);
        return transformActionOrPropertyCNToDBName(canonicalName);
    }

    @Override
    public String createTableDBName(String namespace, String name) {
        String canonicalName = CanonicalNameUtils.createCanonicalName(namespace, name);
        return transformTableCNToDBName(canonicalName);
    }

    @Override
    public String createAutoTableDBName(List<LSFClassSet> classes) {
        StringBuilder builder = new StringBuilder(autoTablesPrefix);
        for (LSFClassSet valueClass : classes) {
            if (valueClass == null) {
                return null;
            }
            builder.append('_');
            // todo [dale]: вообще, это не совсем правильно, в платформе в этом месте вызывается ValueClass.getSID().
            // При стандартной замене всех спец. символов на символ подчеркивания разница не такая большая, но есть 
            builder.append(valueClass.getCanonicalName());
        }
        return cutToMaxLength(transformToIDLettersOnlyFormat(builder.toString()));
    }
    
    // Заменяет знаки '?' на 'null', затем все спец символы заменяет на '_', и удаляет подчеркивания в конце 
    @Override
    public String transformActionOrPropertyCNToDBName(String canonicalName) {
        String dbName = replaceUnknownClassesWithNull(canonicalName);
        dbName = transformToIDLettersOnlyFormat(dbName);
        return cutToMaxLength(dbName);
    }

    protected String transformToIDLettersOnlyFormat(String s) {
        s = replaceAllNonIDLettersWithUnderscore(s);
        return removeTrailingUnderscores(s);
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

    @Override
    public String transformTableCNToDBName(String canonicalName) {
        return cutToMaxLength(canonicalName.replace('.', '_'));
    }

    protected String cutToMaxLength(String s) {
        if (s.length() > maxIDLength) {
            s = s.substring(0, maxIDLength);
        }
        return s;
    }
}
