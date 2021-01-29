package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.psi.LSFStubElement;
import com.lsfusion.lang.psi.LSFStubbedElement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionOrPropStubElement;

public interface LSFExplicitInterfaceProp<This extends LSFExplicitInterfaceProp<This, Stub>, Stub extends LSFStubElement<Stub, This>> extends LSFStubbedElement<This, Stub>, LSFInterfacePropStatement {

    LSFExplicitClasses getExplicitParams();

    LSFActionOrPropDeclaration getDeclaration();

    byte getPropType();

}
