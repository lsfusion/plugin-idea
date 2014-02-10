package com.simpleplugin.classes;

import com.intellij.psi.search.GlobalSearchScope;

public interface LSFClassSet {

    LSFValueClass getCommonClass();

    LSFClassSet op(LSFClassSet set, boolean or);

    boolean containsAll(LSFClassSet set);

    boolean haveCommonChilds(LSFClassSet set, GlobalSearchScope scope);

}
