package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface LSFPropDeclaration extends LSFActionOrPropDeclaration {
    
    LSFClassSet resolveValueClass();

    LSFExClassSet resolveExValueClass(boolean infer);

    LSFExClassSet resolveExValueClassNoCache(boolean infer);

    @Nullable
    List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass); // минимум кол-во параметров мы выведем

    Integer getComplexity();
}
