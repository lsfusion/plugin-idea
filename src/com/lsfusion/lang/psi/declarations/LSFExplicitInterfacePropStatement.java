package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;

import java.util.Set;

public interface LSFExplicitInterfacePropStatement extends LSFExplicitInterfaceActionOrPropStatement<LSFExplicitInterfacePropStatement, ExplicitInterfacePropStubElement> {

    Set<String> getExplicitValues();    
}
