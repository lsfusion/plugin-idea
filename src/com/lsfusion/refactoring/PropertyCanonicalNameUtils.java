package com.lsfusion.refactoring;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.NumericClass;
import com.lsfusion.lang.classes.StaticFormatFileClass;
import com.lsfusion.lang.classes.StringClass;
import com.lsfusion.lang.classes.link.StaticFormatLinkClass;

import java.util.List;

public final class PropertyCanonicalNameUtils {
    static public final String signatureLBracket = "[";
    static public final String signatureRBracket = "]";

    static public final String commonStringClassName = "STRING";
    static public final String commonNumericClassName = "NUMERIC";
    static public final String commonStaticFormatFileClassName = "RAWFILE";
    static public final String commonStaticFormatLinkClassName = "RAWLINK";

    static public final String UNKNOWNCLASS = "?";

    static public final String classDataPropPrefix = "_CLASS_";

    static public String createName(String namespace, String name, List<LSFClassSet> signature) {
        return createName(namespace, name, createSignature(signature));
    }

    /*  Позволяет создавать канонические имена, а также часть канонического имени, передавая
     *  null в качестве пространства имен либо сигнатуры
     */
    static public String createName(String namespace, String name, String signature) {
        StringBuilder builder = new StringBuilder();
        if (namespace != null) {
            appendNamespace(builder, namespace);
        }
        builder.append(name);
        if (signature != null) {
            builder.append(signature);
        }
        return builder.toString();
    }

    static private void appendNamespace(StringBuilder builder, String namespace) {
        builder.append(namespace);
        builder.append(CanonicalNameUtils.DELIMITER);
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
                } else if (cs instanceof StaticFormatFileClass) {
                    snBuilder.append(commonStaticFormatFileClassName);
                } else if (cs instanceof StaticFormatLinkClass) {
                    snBuilder.append(commonStaticFormatLinkClassName);
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
