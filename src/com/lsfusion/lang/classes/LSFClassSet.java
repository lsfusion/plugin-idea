package com.lsfusion.lang.classes;

import com.intellij.psi.search.GlobalSearchScope;

import java.awt.*;

public interface LSFClassSet {

    LSFValueClass getCommonClass();

    LSFClassSet op(LSFClassSet set, boolean or, boolean string);

    boolean containsAll(LSFClassSet set, boolean implicitCast);

    boolean haveCommonChildren(LSFClassSet set, GlobalSearchScope scope);

    int getMinimumWidth(int minCharWidth, FontMetrics fontMetrics);

    int getPreferredWidth(int prefCharWidth, FontMetrics fontMetrics);

    int getMaximumWidth(int maxCharWidth, FontMetrics fontMetrics);

    int getPreferredHeight(FontMetrics fontMetrics);

    int getMaximumHeight(FontMetrics fontMetrics);

    String getCanonicalName();
}
