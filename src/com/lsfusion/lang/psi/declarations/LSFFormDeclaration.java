package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.stubs.FormStubElement;

public interface LSFFormDeclaration extends LSFFormOrActionDeclaration<LSFFormDeclaration, FormStubElement> {
    String getCaption();
}
