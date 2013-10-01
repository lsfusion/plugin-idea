package com.simpleplugin.psi.declarations;

import com.simpleplugin.classes.LSFClassSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Map;

public interface LSFPropDeclaration extends LSFDeclaration {
    
    LSFClassSet resolveValueClass(boolean infer);
    
    @Nullable
    List<LSFClassSet> resolveParamClasses();
    
    @Nullable
    List<LSFClassSet> inferParamClasses(LSFClassSet valueClass); // минимум кол-во параметров мы выведем
}
