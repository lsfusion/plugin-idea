package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;

public interface LSFFormReference extends LSFFullNameReference<LSFFormDeclaration, LSFFormDeclaration> {
    LSFId getFormUsageNameIdentifier();
}
