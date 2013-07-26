package com.simpleplugin.psi.references;

import com.intellij.util.Query;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;

import java.util.List;

public interface LSFGlobalReference<T extends LSFGlobalDeclaration> extends LSFReference {

    T resolve();

    String getNameRef();

    Query<T> resolveNoCache();
}
