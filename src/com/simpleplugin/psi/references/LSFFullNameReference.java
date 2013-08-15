package com.simpleplugin.psi.references;

import com.simpleplugin.psi.declarations.LSFDeclaration;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;

public interface LSFFullNameReference<T extends LSFDeclaration, G extends LSFFullNameDeclaration> extends LSFGlobalReference<T> {
}
