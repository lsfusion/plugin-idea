package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Set;

public interface LSFActionOrPropDeclaration extends LSFDeclaration, LSFInterfacePropStatement {

    boolean isAbstract();

    @Nullable
    List<LSFExClassSet> resolveExParamClasses();

    List<LSFExClassSet> resolveExParamClassesNoCache();

    @Nullable
    List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass); // минимум кол-во параметров мы выведем
}
