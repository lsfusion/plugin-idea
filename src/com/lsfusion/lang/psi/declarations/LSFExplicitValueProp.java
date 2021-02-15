package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.LSFElement;
import com.lsfusion.lang.psi.LSFStubElement;
import com.lsfusion.lang.psi.LSFStubbedElement;

public interface LSFExplicitValueProp<This extends LSFExplicitValueProp<This, Stub>, Stub extends LSFStubElement<Stub, This>> extends LSFStubbedElement<This, Stub> {

    LSFGlobalPropDeclaration getDeclaration();

}
