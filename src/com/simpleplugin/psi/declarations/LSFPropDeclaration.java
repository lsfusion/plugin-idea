package com.simpleplugin.psi.declarations;

import com.simpleplugin.classes.LSFClassSet;

import java.util.List;

public interface LSFPropDeclaration extends LSFDeclaration {
    
    LSFClassSet resolveValueClass();
    
    List<LSFClassSet> resolveParamClasses();
}
