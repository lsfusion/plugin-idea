package com.lsfusion.lang;

import com.lsfusion.lang.classes.*;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Set;

/**
 * Reference:
 *   lsfusion.server.logics.DefaultSIDPolicy#transformCanonicalNameToSID
 *   lsfusion.server.logics.PropertyCanonicalNameUtils
 *   lsfusion.server.logics.ClassCanonicalNameUtils
 */
public class DefaultColumnNamingPolicy extends ColumnNamingPolicy {
    static public final String commonStringClassName = "STRING";
    static public final String commonNumericClassName = "NUMERIC";

    public static final String ConcatenateClassNameLBracket = "(";
    public static final String ConcatenateClassNameRBracket = ")";
    public static final String ConcatenateClassNamePrefix = "CONCAT";

    public static final String OrObjectClassSetNameLBracket = "{";
    public static final String OrObjectClassSetNameRBracket = "}";

    public static final String UpClassSetNameLBracket = "(";
    public static final String UpClassSetNameRBracket = ")";

    @Override
    public String getColumnName(LSFGlobalPropDeclaration declaration) {
        List<LSFClassSet> classes = declaration.resolveParamClasses();
        String classPostfix = "";
        if (classes != null) {
            for (LSFClassSet classSet : classes) {
                if (!classPostfix.isEmpty()) {
                    classPostfix += "_";
                }
                classPostfix += getCanonicalClassName(classSet);
            }
            if (!classPostfix.isEmpty()) {
                classPostfix = "_" + classPostfix;
            }
        }

        return declaration.getNamespaceName() + "_" + declaration.getName() + classPostfix;
    }

    private String getCanonicalClassName(LSFClassSet classSet) {
        if (classSet == null) {
            return "null";
        }
        
        if (classSet instanceof StringClass) {
            return commonStringClassName;
        } else if (classSet instanceof NumericClass) {
            return commonNumericClassName;
        } else if (classSet instanceof DataClass) {
            return ((DataClass) classSet).getName();
        } else if (classSet instanceof ConcatenateClassSet) {
            return getCanonicalClassName((ConcatenateClassSet)classSet);
        } else if (classSet instanceof CustomClassSet) {
            return getCanonicalClassName((CustomClassSet)classSet);
        }
        
        throw new IllegalStateException("shouldn't happen");
    }

    // CONCAT(CN1, ..., CNk)
    private String getCanonicalClassName(@NotNull ConcatenateClassSet ccs) {
        String sid = ConcatenateClassNamePrefix + ConcatenateClassNameLBracket;
        for (int i = 0; i < ccs.getSetSize(); i++) {
            LSFClassSet set = ccs.getSet(i);
            sid += (sid.length() > 1 ? "," : "");
            sid += getCanonicalClassName(set);
        }
        return sid + ConcatenateClassNameRBracket;
    }


    // todo: make determenistic accordingly to server logic
    // as UpClassSet (CN1, ..., CNk)
    private String getCanonicalClassName(@NotNull CustomClassSet classSet) {
        Set<LSFClassDeclaration> classes = classSet.getClasses();
        if (classes.size() == 1) {
            return classes.iterator().next().getName();
        }

        String sid = UpClassSetNameLBracket;
        for (LSFClassDeclaration cls : classes) {
            sid += (sid.length() > 1 ? "," : "");
            sid += cls.getName();
        }
        return sid + UpClassSetNameRBracket;
    }
}
