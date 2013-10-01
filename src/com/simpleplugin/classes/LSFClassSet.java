package com.simpleplugin.classes;

public interface LSFClassSet {
    
    LSFValueClass getCommonClass();
    
    LSFClassSet op(LSFClassSet set, boolean or);
    
    boolean containsAll(LSFClassSet set);

    boolean haveCommonChilds(LSFClassSet set);

}
