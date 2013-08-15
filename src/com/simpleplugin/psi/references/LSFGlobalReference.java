package com.simpleplugin.psi.references;

import com.intellij.util.Query;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleName;
import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;

import java.util.List;

public interface LSFGlobalReference<T extends LSFDeclaration> extends LSFReference<T> {
}
