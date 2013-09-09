package com.simpleplugin.psi.declarations;

import com.simpleplugin.classes.LSFClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LSFPropDeclaration extends LSFDeclaration {
    
    LSFClassSet resolveValueClass();
    
    @Nullable
    List<LSFClassSet> resolveParamClasses();
}
