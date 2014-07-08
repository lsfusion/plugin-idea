package com.lsfusion.lang.psi.references;

import com.intellij.openapi.util.Condition;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;

public interface LSFFullNameReference<T extends LSFDeclaration, G extends LSFFullNameDeclaration> extends LSFGlobalReference<T> {

    Condition<G> getCondition();

    Condition<G> getFullCondition();

    String getFullNameRef();

    void setFullNameRef(String name, MetaTransaction transaction);
}
