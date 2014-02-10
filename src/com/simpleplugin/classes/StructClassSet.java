package com.simpleplugin.classes;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;

import java.util.Arrays;

public class StructClassSet implements LSFClassSet, LSFValueClass {

    private LSFClassSet[] sets;

    public StructClassSet(LSFClassSet[] sets) {
        this.sets = sets;
    }

    public LSFClassSet get(int i) {
        if (i < sets.length && i >= 0)
            return sets[i];
        return null;
    }

    public LSFClassSet op(LSFClassSet set, boolean or) {
        if (!(set instanceof StructClassSet))
            return null;
        StructClassSet structSet = (StructClassSet) set;
        if (structSet.sets.length != sets.length)
            return null;

        LSFClassSet[] result = new LSFClassSet[sets.length];
        for (int i = 0; i < sets.length; i++)
            result[i] = sets[i].op(structSet.sets[i], or);
        return new StructClassSet(result);
    }

    public boolean containsAll(LSFClassSet set) {
        if (!(set instanceof StructClassSet))
            return false;
        StructClassSet structSet = (StructClassSet) set;
        if (structSet.sets.length != sets.length)
            return false;

        for (int i = 0; i < sets.length; i++)
            if (!sets[i].containsAll(structSet.sets[i]))
                return false;
        return true;
    }

    public boolean haveCommonChilds(LSFClassSet set, GlobalSearchScope scope) {
        if (!(set instanceof StructClassSet))
            return false;
        StructClassSet structSet = (StructClassSet) set;
        if (structSet.sets.length != sets.length)
            return false;

        for (int i = 0; i < sets.length; i++)
            if (!sets[i].haveCommonChilds(structSet.sets[i], scope))
                return false;
        return true;
    }

    // пока не поддерживаем

    public LSFValueClass getCommonClass() {
        throw new UnsupportedOperationException("getCommonClass() isn't supproted for StructClassSet");
    }

    public String getQName(PsiElement context) {
        throw new UnsupportedOperationException("getQName(PsiElement context) isn't supproted for StructClassSet");
    }

    @Override
    public String getName() {
        throw new UnsupportedOperationException("getName() isn't supproted for StructClassSet");
    }

    public boolean equals(Object o) {
        return this == o || o instanceof StructClassSet && Arrays.equals(sets, ((StructClassSet) o).sets);
    }

    public int hashCode() {
        return Arrays.hashCode(sets);
    }
}
