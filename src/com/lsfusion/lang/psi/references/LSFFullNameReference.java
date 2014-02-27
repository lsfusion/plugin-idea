package com.lsfusion.psi.references;

import com.lsfusion.psi.declarations.LSFDeclaration;
import com.lsfusion.psi.declarations.LSFFullNameDeclaration;

public interface LSFFullNameReference<T extends LSFDeclaration, G extends LSFFullNameDeclaration> extends LSFGlobalReference<T> {
}
