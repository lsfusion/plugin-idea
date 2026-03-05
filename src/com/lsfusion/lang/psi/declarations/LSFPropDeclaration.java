package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.declarations.impl.LSFStatementGlobalPropDeclarationImpl;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LSFPropDeclaration extends LSFActionOrPropDeclaration {

    @Nullable
    List<LSFExClassSet> inferParamClasses(LSFExClassSet valueClass); // минимум кол-во параметров мы выведем

    default Integer getComplexity() {
        return LSFStatementGlobalPropDeclarationImpl.getPropComplexity(this);
    }
}
