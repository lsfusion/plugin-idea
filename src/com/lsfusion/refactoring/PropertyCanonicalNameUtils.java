package com.lsfusion.refactoring;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.NumericClass;
import com.lsfusion.lang.classes.StringClass;

import java.util.List;

public final class PropertyCanonicalNameUtils {
    static public final String signatureLBracket = "[";
    static public final String signatureRBracket = "]";

    static public final String commonStringClassName = "STRING";
    static public final String commonNumericClassName = "NUMERIC";

    static public final String UNKNOWNCLASS = "?";

    static public final String classDataPropPrefix = "_CLASS_";
    static public final String policyPropPrefix = "_POLICY_";
    static public final String logPropPrefix = "_LOG_";
    static public final String drillDownPrefix = "_DRILLDOWN_";

    static public String createName(String namespace, String name, List<LSFClassSet> signature) {
        return createName(namespace, name, createSignature(signature));
    }
    
    static public String createName(String namespace, String name, String signature) {
        StringBuilder builder = new StringBuilder();
        builder.append(namespace);
        builder.append(".");
        builder.append(name);
        if (signature != null) {
            builder.append(signature);
        }
        return builder.toString();
    }

    private static String createSignature(List<LSFClassSet> signature) {
        StringBuilder snBuilder = new StringBuilder();
        snBuilder.append(signatureLBracket);
        boolean isFirst = true;
        if (signature != null) {
            for (LSFClassSet cs : signature) {
                if (!isFirst) {
                    snBuilder.append(",");
                }
                isFirst = false;
                if (cs instanceof StringClass) {
                    snBuilder.append(commonStringClassName);
                } else if (cs instanceof NumericClass) {
                    snBuilder.append(commonNumericClassName);
                } else if (cs != null) {
                    snBuilder.append(cs.getCanonicalName());
                } else {
                    snBuilder.append(UNKNOWNCLASS);
                }
            }
        }
        snBuilder.append(signatureRBracket);
        return snBuilder.toString();
    }
}
