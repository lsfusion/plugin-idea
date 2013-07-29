package com.simpleplugin.psi.references;

import com.intellij.util.Query;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;

import java.util.List;

public interface LSFGlobalReference<T extends LSFGlobalDeclaration> extends LSFReference {

    LSFId resolve();
    T resolveDecl();

    String getNameRef();

    Query<T> resolveNoCache();
}
