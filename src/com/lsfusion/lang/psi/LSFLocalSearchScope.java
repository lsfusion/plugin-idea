package com.lsfusion.lang.psi;

import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.references.LSFReference;

public class LSFLocalSearchScope {

    public static final LSFLocalSearchScope GLOBAL = null;

    public static LSFLocalSearchScope createFrom(LSFExtend extend) {
//        dsds
        return null;
    }

    public static LSFLocalSearchScope createFrom(LSFReference extend) {
        return null;
    }

    public static LSFLocalSearchScope createFrom(LSFDeclaration extend) {
        return null;
    }

    public static LSFLocalSearchScope createFrom(LSFContextFiltersClause extend) {
        return null;
    }

//    equals для designview
}
