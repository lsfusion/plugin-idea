package com.lsfusion.lang.classes;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
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
    public int getMinimumWidth(int minCharWidth, FontMetrics fontMetrics) {
        return fontMetrics.stringWidth("999 999") + 8;
    }

    @Override
    public int getPreferredWidth(int prefCharWidth, FontMetrics fontMetrics) {
        return fontMetrics.stringWidth("9 999 999") + 8;
    }

    @Override
    public int getMaximumWidth(int maxCharWidth, FontMetrics fontMetrics) {
        return getPreferredWidth(0, fontMetrics);
    }

    @Override
    public int getPreferredHeight(FontMetrics fontMetrics) {
        return fontMetrics.getHeight() + 1;
    }

    @Override
    public int getMaximumHeight(FontMetrics fontMetrics) {
        return getPreferredHeight(fontMetrics);
    }

    // пока не поддерживаем

    public LSFValueClass getCommonClass() {
        throw new UnsupportedOperationException("getCommonClass() isn't supproted for StructClassSet");
    }

    public String getQName(PsiElement context) {
        throw new UnsupportedOperationException("getQName(PsiElement context) isn't supproted for StructClassSet");
    }

    @Override
    public String getCaption() {
        return null;
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("getName() isn't supproted for StructClassSet");
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
}
