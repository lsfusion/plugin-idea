package com.simpleplugin.psi.declarations;

import com.simpleplugin.classes.LSFClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LSFPropDeclaration extends LSFDeclaration {

    String getSignaturePresentableText();

    LSFClassSet resolveValueClass(boolean infer);

    LSFClassSet resolveValueClassNoCache(boolean infer);

    @Nullable
    List<LSFClassSet> resolveParamClasses();

    List<LSFClassSet> resolveParamClassesNoCache();

    @Nullable
    List<LSFClassSet> inferParamClasses(LSFClassSet valueClass); // минимум кол-во параметров мы выведем
}
