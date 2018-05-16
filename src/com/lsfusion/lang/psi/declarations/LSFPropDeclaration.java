package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.cache.ValueClassCache;
import com.lsfusion.lang.psi.declarations.impl.LSFActionOrGlobalPropDeclarationImpl;
import com.lsfusion.lang.psi.declarations.impl.LSFStatementGlobalPropDeclarationImpl;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LSFPropDeclaration extends LSFActionOrPropDeclaration {
    
    default LSFClassSet resolveValueClass() {
        return LSFExClassSet.fromEx(resolveExValueClass(false));
    }

    default LSFExClassSet resolveExValueClass(boolean infer) {
        return ValueClassCache.getInstance(getProject()).resolveValueClassWithCaching(this, infer);
    }

    LSFExClassSet resolveExValueClassNoCache(boolean infer);

    @Nullable
    List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass); // минимум кол-во параметров мы выведем

    default Integer getComplexity() {
        return LSFStatementGlobalPropDeclarationImpl.getPropComplexity(this);
    }
}
