package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;

public interface LSFFullNameReference<T extends LSFDeclaration, G extends LSFFullNameDeclaration> extends LSFGlobalReference<T> {
}
