package com.lsfusion.refactoring;

import com.lsfusion.lang.classes.ConcatenateClassSet;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;

import java.util.Set;

public class ClassCanonicalNameUtils {

    public static final String ConcatenateClassNameLBracket = "(";
    public static final String ConcatenateClassNameRBracket = ")";
    public static final String ConcatenateClassNamePrefix = "CONCAT";

    public static final String UpClassSetNameLBracket = "(";
    public static final String UpClassSetNameRBracket = ")";

    // CONCAT(CN1, ..., CNk)
    public static String createName(ConcatenateClassSet ccs) {
        LSFClassSet[] classes = ccs.getSets();
        String sid = ConcatenateClassNamePrefix + ConcatenateClassNameLBracket;
        for (LSFClassSet set : classes) {
            sid += (sid.length() > 1 ? "," : "");
            sid += set.getCanonicalName();
        }
        return sid + ConcatenateClassNameRBracket;
    }

    // (CN1, ..., CNk) 
    public static String createName(CustomClassSet up) {
        Set<LSFClassDeclaration> classes = up.getClasses();
        if (classes.size() == 1) {
            return classes.iterator().next().getCanonicalName();
        }
        String sid = UpClassSetNameLBracket;
        for (LSFClassDeclaration cls : classes) {
            sid += (sid.length() > 1 ? "," : "");
            sid += cls.getCanonicalName();
        }
        return sid + UpClassSetNameRBracket;
    }
}
