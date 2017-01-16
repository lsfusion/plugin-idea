package com.lsfusion.lang.psi;

import com.lsfusion.lang.classes.ExtInt;
import com.lsfusion.lang.classes.StringClass;

public enum LSFReflectionType {
    CANONICALNAME;
    
    public static final StringClass CANONICAL_NAME_VALUE_CLASS = new StringClass(false, false, new ExtInt(200));
}
