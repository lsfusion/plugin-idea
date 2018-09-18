package com.lsfusion.lang.classes;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.design.model.PropertyDrawView;
import com.lsfusion.refactoring.ClassCanonicalNameUtils;

import java.awt.*;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ConcatenateClassSet implements LSFClassSet, LSFValueClass {

    private final LSFClassSet[] sets;

    public ConcatenateClassSet(LSFClassSet[] sets) {
        this.sets = sets;
    }

    public LSFClassSet getSet(int i) {
        if (i < sets.length && i >= 0)
            return sets[i];
        return null;
    }

    public LSFClassSet[] getSets() {
        return sets;
    }

    public int getSetSize() {
        return sets.length;
    }

    public LSFClassSet op(LSFClassSet set, boolean or, boolean string) {
        if (!(set instanceof ConcatenateClassSet))
            return null;
        ConcatenateClassSet structSet = (ConcatenateClassSet) set;
        if (structSet.sets.length != sets.length)
            return null;

        LSFClassSet[] result = new LSFClassSet[sets.length];
        for (int i = 0; i < sets.length; i++)
            result[i] = sets[i].op(structSet.sets[i], or, string);
        return new ConcatenateClassSet(result);
    }

    public boolean containsAll(LSFClassSet set, boolean implicitCast) {
        if (!(set instanceof ConcatenateClassSet))
            return false;
        ConcatenateClassSet structSet = (ConcatenateClassSet) set;
        if (structSet.sets.length != sets.length)
            return false;

        for (int i = 0; i < sets.length; i++)
            if (!sets[i].containsAll(structSet.sets[i], true))
                return false;
        return true;
    }

    public boolean haveCommonChildren(LSFClassSet set, GlobalSearchScope scope) {
        if (!(set instanceof ConcatenateClassSet))
            return false;
        ConcatenateClassSet structSet = (ConcatenateClassSet) set;
        if (structSet.sets.length != sets.length)
            return false;

        for (int i = 0; i < sets.length; i++)
            if (!sets[i].haveCommonChildren(structSet.sets[i], scope))
                return false;
        return true;
    }

    @Override
    public boolean isCompatible(LSFClassSet set) {
        return set instanceof ConcatenateClassSet;
    }

    @Override
    public boolean isAssignable(LSFClassSet set) {
        return isCompatible(set);
    }

    @Override
    public boolean isFlex() {
        return false;
    }

    @Override
    public int getDefaultWidth(FontMetrics fontMetrics, PropertyDrawView propertyDraw) {
        return 40;
    }

    @Override
    public int getDefaultHeight(FontMetrics fontMetrics, int numRowHeight) {
        return fontMetrics.getHeight() * numRowHeight + 1;
    }

    // пока не поддерживаем

    public LSFValueClass getCommonClass() {
        throw new UnsupportedOperationException("getCommonClass() isn't supported for StructClassSet");
    }

    public String getQName(PsiElement context) {
        throw new UnsupportedOperationException("getQName(PsiElement context) isn't supported for StructClassSet");
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("getName() isn't supported for StructClassSet");
    }

    public boolean equals(Object o) {
        return this == o || o instanceof ConcatenateClassSet && Arrays.equals(sets, ((ConcatenateClassSet) o).sets);
    }

    public int hashCode() {
        return Arrays.hashCode(sets);
    }

    @Override
    public List<String> getSNames() {
        return Collections.singletonList("Struct");
    }

    @Override
    public String getCanonicalName() {
        return ClassCanonicalNameUtils.createName(this);
    }

    @Override
    public int getFullWidthString(String widthString, FontMetrics fontMetrics) {
        return fontMetrics.stringWidth(widthString) + 8;
    }

    @Override
    public LSFClassSet getUpSet() {
        return this;
    }
}
