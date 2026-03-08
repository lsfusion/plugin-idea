package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFFormUsage;
import com.lsfusion.lang.psi.context.FormContext;

public interface LSFFormFormsDeclaration extends LSFFormElementDeclaration<LSFFormFormsDeclaration>, FormContext {
    LSFFormUsage getFormUsage();
}
