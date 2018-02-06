package com.lsfusion.lang.classes;

import com.intellij.psi.search.GlobalSearchScope;

import java.awt.*;

public interface LSFClassSet {

    LSFValueClass getCommonClass();

    LSFClassSet op(LSFClassSet set, boolean or, boolean string);

    boolean containsAll(LSFClassSet set, boolean implicitCast);

    boolean haveCommonChildren(LSFClassSet set, GlobalSearchScope scope);

    boolean isCompatible(LSFClassSet set);

    boolean isAssignable(LSFClassSet set);

    boolean isFlex();

    int getWidth(int minCharWidth, FontMetrics fontMetrics);

    int getHeight(FontMetrics fontMetrics);

    String getCanonicalName();
}
