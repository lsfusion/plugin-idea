package com.lsfusion.lang.psi.declarations;

import com.lsfusion.documentation.LSFDocumentation;
import com.lsfusion.lang.psi.stubs.FormStubElement;

public interface LSFFormDeclaration extends LSFFormOrActionDeclaration<LSFFormDeclaration, FormStubElement>, LSFDocumentation {
    String getCaption();
}
