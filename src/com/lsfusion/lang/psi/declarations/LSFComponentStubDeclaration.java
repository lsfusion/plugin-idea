package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFComponentDecl;
import com.lsfusion.lang.psi.stubs.ComponentStubElement;

public interface LSFComponentStubDeclaration extends StubBasedPsiElement<ComponentStubElement> {
    LSFComponentDecl getComponentDecl();
}
