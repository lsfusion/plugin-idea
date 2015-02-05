package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface LSFPropDeclaration extends LSFDeclaration {

    String getSignaturePresentableText();

    boolean isAbstract();
    
    List<LSFClassSet> resolveParamClasses();
    
    LSFClassSet resolveValueClass();
    

    LSFExClassSet resolveExValueClass(boolean infer);

    LSFExClassSet resolveExValueClassNoCache(boolean infer);

    @Nullable
    List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass); // минимум кол-во параметров мы выведем

    @Nullable
    List<LSFExClassSet> resolveExParamClasses();

    List<LSFExClassSet> resolveExParamClassesNoCache();

    Set<LSFPropDeclaration> getDependencies();

    Set<LSFPropDeclaration> getDependents();
    
    Integer getComplexity();
}
