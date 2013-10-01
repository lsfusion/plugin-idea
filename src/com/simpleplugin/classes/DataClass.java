package com.simpleplugin.classes;

import com.intellij.psi.PsiElement;

public abstract class DataClass implements LSFClassSet, LSFValueClass {

    public static final DataClass BOOLEAN = new SimpleDataClass("BOOLEAN");

    public static final DataClass STRUCT = new SimpleDataClass("STRUCT");
    
    public abstract DataClass op(DataClass compClass, boolean or);

    public LSFValueClass getCommonClass() {
        return this;
    }

    protected abstract String getName(); 
    @Override
    public String getQName(PsiElement context) {
        return getName();
    }

    @Override
    public LSFClassSet op(LSFClassSet set, boolean or) {
        if(!(set instanceof DataClass))
            return null;
        
        return op((DataClass)set, or);
    }

    @Override
    public boolean containsAll(LSFClassSet set) {
        if(!(set instanceof DataClass))
            return false;
        return op((DataClass) set, false) != null;
    }

    @Override
    public boolean haveCommonChilds(LSFClassSet set) {
        if(!(set instanceof DataClass))
            return false;
        
        return op((DataClass) set, false) != null;
    }

    @Override
    public String toString() {
        return getName();
    }
}
