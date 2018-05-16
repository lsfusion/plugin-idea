package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.StubElement;

public interface LSFExplicitValueProp<Stub extends StubElement> extends StubBasedPsiElement<Stub> {

    LSFGlobalPropDeclaration getDeclaration();

}
