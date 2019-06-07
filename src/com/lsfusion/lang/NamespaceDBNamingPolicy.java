package com.lsfusion.lang;

import com.lsfusion.refactoring.CompoundNameUtils;
import com.lsfusion.refactoring.PropertyCanonicalNameParser;

public class NamespaceDBNamingPolicy extends FixedSizeUnderscoreDBNamingPolicy {
    public NamespaceDBNamingPolicy(int maxIDLength) {
        super(maxIDLength, "_auto");
    }

    @Override
    public String transformActionOrPropertyCNToDBName(String canonicalName) {
        String namespace = PropertyCanonicalNameParser.getNamespace(canonicalName);
        String name = PropertyCanonicalNameParser.getName(canonicalName);
        String compoundName = CompoundNameUtils.createName(namespace, name);
        return cutToMaxLength(transformToIDLettersOnlyFormat(compoundName));
    }
}
