package com.lsfusion.psi.declarations;

import com.lsfusion.classes.LSFClassSet;
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
