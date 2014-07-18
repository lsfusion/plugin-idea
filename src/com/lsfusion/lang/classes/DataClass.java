package com.lsfusion.lang.classes;

import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import org.apache.commons.lang.StringUtils;
import com.lsfusion.util.BaseUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.awt.*;

public abstract class DataClass implements LSFClassSet, LSFValueClass {

    public static final DataClass STRUCT = new SimpleDataClass("STRUCT");

    public LSFValueClass getCommonClass() {
        return this;
    }

    public abstract String getName();

    @Override
    public List<String> getSNames() {
        String name = getName();
        List<String> result = new ArrayList<String>();
        result.add(name);
        result.add(StringUtils.capitalize(name.toLowerCase()));
        result.addAll(getExtraNames());
        return result;
    }

    public Collection<String> getExtraNames() {
        return new ArrayList<String>();
    }

    public DataClass op(DataClass compClass, boolean or) {
        if (compClass.equals(this))
            return this;
        return null;
    }

    @Override
    public String getQName(PsiElement context) {
        return getName();
    }

    @Override
    public LSFClassSet op(LSFClassSet set, boolean or) {
        if (!(set instanceof DataClass))
            return null;

        return op((DataClass) set, or);
    }

    @Override
    public boolean containsAll(LSFClassSet set) {
        if (!(set instanceof DataClass))
            return false;
        return op((DataClass) set, false) != null;
    }

    @Override
    public boolean haveCommonChildren(LSFClassSet set, GlobalSearchScope scope) {
        if (!(set instanceof DataClass))
            return false;

        return op((DataClass) set, false) != null;
    }

    @Override
    public String toString() {
        return getName();
    }

    public int getMinimumWidth(int minCharWidth, FontMetrics fontMetrics) {
        String minMask = minCharWidth != 0
                ? BaseUtils.replicate('0', minCharWidth)
                : getMinimumMask();

        return fontMetrics.stringWidth(minMask) + 8;
    }

    public int getPreferredWidth(int prefCharWidth, FontMetrics fontMetrics) {
        String prefMask = prefCharWidth != 0
                ? BaseUtils.replicate('0', prefCharWidth)
                : getPreferredMask();

        return fontMetrics.stringWidth(prefMask) + 8;
    }

    public int getPreferredHeight(FontMetrics fontMetrics) {
        return fontMetrics.getHeight() + 1;
    }

    public int getMaximumWidth(int maxCharWidth, FontMetrics fontMetrics) {
        if (maxCharWidth != 0)
            return fontMetrics.stringWidth(BaseUtils.replicate('0', maxCharWidth)) + 8;
        else
            return Integer.MAX_VALUE;
    }

    public int getMaximumHeight(FontMetrics fontMetrics) {
        return getPreferredHeight(fontMetrics);
    }

    public String getMinimumMask() {
        return getPreferredMask();
    }

    public abstract String getPreferredMask();
}
