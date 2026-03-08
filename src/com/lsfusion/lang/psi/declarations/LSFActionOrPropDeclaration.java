package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.psi.cache.ParamClassesCache;
import com.lsfusion.lang.psi.cache.ValueClassCache;
import com.lsfusion.lang.psi.declarations.impl.LSFActionOrGlobalPropDeclarationImpl;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LSFActionOrPropDeclaration extends LSFDeclaration, LSFInterfacePropStatement {

    boolean isAbstract();

    default LSFExClassSet resolveExValueClass(boolean infer) {
        return ValueClassCache.getInstance(getProject()).resolveValueClassWithCaching(this, infer);
    }

    default LSFExClassSet resolveExValueClassNoCache(boolean infer) {
        return null;
    }

    @Nullable
    default List<LSFExClassSet> resolveExParamClasses() {
        return ParamClassesCache.getInstance(getProject()).resolveParamClassesWithCaching(this);
    }

    List<LSFExClassSet> resolveExParamClassesNoCache();

    default String getParamPresentableText() {
        return LSFActionOrGlobalPropDeclarationImpl.getParamPresentableText(resolveParamClasses());
    }

    default List<LSFClassSet> resolveParamClasses() {
        return LSFExClassSet.fromEx(resolveExParamClasses());
    }

    @Nullable
    List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass); // минимум кол-во параметров мы выведем

    default LSFExClassSet resolveJoinValueClass(boolean infer) {
        return resolveExValueClass(infer);
    }

    default List<LSFClassSet> resolveJoinParamClasses() {
        return resolveParamClasses();
    }
}
