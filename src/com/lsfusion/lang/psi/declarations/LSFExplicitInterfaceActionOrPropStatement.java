package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionOrPropStubElement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;

import java.util.Set;

public interface LSFExplicitInterfaceActionOrPropStatement<This extends LSFExplicitInterfaceActionOrPropStatement<This, Stub>, Stub extends ExplicitInterfaceActionOrPropStubElement<Stub, This>> extends LSFExplicitInterfaceProp<This, Stub> {
}
