package com.lsfusion.lang.classes;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.util.BaseUtils;
import org.apache.commons.lang.StringUtils;

import java.awt.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class DataClass implements LSFClassSet, LSFValueClass {

    public static final DataClass STRUCT = new SimpleDataClass("STRUCT");

    public LSFValueClass getCommonClass() {
        return this;
    }

    public abstract String getName();

    public boolean fixedSize() {
        return true;
    }

    @Override
    public boolean isFlex() {
        return true;
    }

    @Override
    public List<String> getSNames() {
        String name = getName();
        List<String> result = new ArrayList<>();
        result.add(name);
        result.add(StringUtils.capitalize(name.toLowerCase()));
        result.addAll(getExtraNames());
        return result;
    }

    public Collection<String> getExtraNames() {
        return new ArrayList<>();
    }

    public DataClass op(DataClass compClass, boolean or, boolean string) {
        assert or || !string;
        if (compClass.equals(this))
            return this;
        return null;
    }

    @Override
    public String getQName(PsiElement context) {
        return getName();
    }

    @Override
    public LSFClassSet op(LSFClassSet set, boolean or, boolean string) {
        if (!(set instanceof DataClass))
            return null;

        return op((DataClass) set, or, string);
    }

    @Override
    public boolean containsAll(LSFClassSet set, boolean implicitCast) {
        if (!(set instanceof DataClass))
            return false;
        DataClass compatible = op((DataClass) set, true, false);
        if(implicitCast)
            return compatible != null;
        return compatible !=null && compatible.equals(this);
    }

    @Override
    public boolean haveCommonChildren(LSFClassSet set, GlobalSearchScope scope) {
        if (!(set instanceof DataClass))
            return false;

        return op((DataClass) set, false, false) != null;
    }

    @Override
    public boolean isCompatible(LSFClassSet set) {
        return haveCommonChildren(set, null);
    }

    @Override
    public boolean isAssignable(LSFClassSet set) {
        return true;
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getWidth(int minCharWidth, FontMetrics fontMetrics) {
        String minMask = minCharWidth != 0
                ? BaseUtils.replicate('0', minCharWidth)
                : getMask();

        return fontMetrics.stringWidth(minMask) + 8;
    }

    public int getHeight(FontMetrics fontMetrics) {
        return fontMetrics.getHeight() + 1;
    }

    public abstract String getMask();

    @Override
    public String getCanonicalName() {
        return getName();
    }
}
