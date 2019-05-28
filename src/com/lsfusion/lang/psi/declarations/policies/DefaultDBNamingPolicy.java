package com.lsfusion.lang.psi.declarations.policies;

import com.lsfusion.refactoring.PropertyCanonicalNameUtils;

public class DefaultDBNamingPolicy extends FixedSizeUnderscoreDBNamingPolicy {
    public DefaultDBNamingPolicy(int maxIDLength) {
        super(maxIDLength, "_auto");
    }

    private static final char DELIMITER = '.';
    @Override
    public String transformActionOrPropertyCNToDBName(String canonicalName) {
        int signaturePos = canonicalName.indexOf(PropertyCanonicalNameUtils.signatureLBracket);
        assert signaturePos > 0;
        
        // отдельно обрабатываем канонические имена class data properties из-за того, что они получаются слишком длинными, 
        // а сигнатура в них необязательна для уникальности
        if (canonicalName.startsWith("System." + PropertyCanonicalNameUtils.classDataPropPrefix)) {
            return cutToMaxLength(nameWithoutSignature(canonicalName, signaturePos).replace(DELIMITER, '_'));
        }
        
        String signatureStr = canonicalName.substring(signaturePos);
        signatureStr = removeNamespacesFromSignatureClasses(signatureStr);
        
        return super.transformActionOrPropertyCNToDBName(canonicalName.substring(0, signaturePos) + signatureStr);
    }
    
    private String removeNamespacesFromSignatureClasses(String signatureStr) {
        return signatureStr.replaceAll("[a-zA-Z0-9_]+\\.", "");
    } 
    
    private String nameWithoutSignature(String name, int signaturePos) {
        return name.substring(0, signaturePos);
    }
}