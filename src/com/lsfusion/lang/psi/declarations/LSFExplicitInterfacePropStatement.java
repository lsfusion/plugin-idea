package com.lsfusion.lang.psi.declarations;

import com.intellij.psi.StubBasedPsiElement;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfacePropStubElement;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public interface LSFExplicitInterfacePropStatement extends LSFExplicitInterfaceActionOrPropStatement<ExplicitInterfacePropStubElement> {

    Set<String> getExplicitValues();    
}
