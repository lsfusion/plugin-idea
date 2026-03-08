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
        return resolveExParamClasses(false);
    }

    default List<LSFExClassSet> resolveExParamClasses(boolean joinAction) {
        return ParamClassesCache.getInstance(getProject()).resolveParamClassesWithCaching(this, joinAction);
    }

    List<LSFExClassSet> resolveExParamClassesNoCache(boolean joinAction);

    default String getParamPresentableText() {
        return LSFActionOrGlobalPropDeclarationImpl.getParamPresentableText(resolveParamClasses());
    }

    default List<LSFClassSet> resolveParamClasses() {
        return resolveParamClasses(false);
    }

    default List<LSFClassSet> resolveParamClasses(boolean joinAction) {
        return LSFExClassSet.fromEx(resolveExParamClasses(joinAction));
    }

    @Nullable
    List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass); // минимум кол-во параметров мы выведем

    default LSFExClassSet resolveJoinValueClass(boolean infer) {
        return resolveExValueClass(infer);
    }

    default List<LSFClassSet> resolveJoinParamClasses() {
        return resolveParamClasses(true);
    }
}
