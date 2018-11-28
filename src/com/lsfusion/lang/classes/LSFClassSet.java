package com.lsfusion.lang.classes;

import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.design.model.PropertyDrawView;

import java.awt.*;

public interface LSFClassSet {

    LSFValueClass getCommonClass();

    LSFClassSet op(LSFClassSet set, boolean or, boolean string);

    boolean containsAll(LSFClassSet set, boolean implicitCast);

    boolean haveCommonChildren(LSFClassSet set, GlobalSearchScope scope);

    boolean isCompatible(LSFClassSet set);

    boolean isAssignable(LSFClassSet set);

    boolean isFlex();

    int getFullWidthString(String widthString, FontMetrics fontMetrics);

    int getDefaultWidth(FontMetrics fontMetrics, PropertyDrawView propertyDraw);

    int getDefaultHeight(FontMetrics fontMetrics, int charHeight);

    String getCanonicalName();
}
