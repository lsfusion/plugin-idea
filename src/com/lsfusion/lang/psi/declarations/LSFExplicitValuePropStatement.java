package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitValueStubElement;
import org.jetbrains.annotations.NotNull;

public interface LSFExplicitValuePropStatement extends LSFExplicitValueProp<LSFExplicitValuePropStatement, ExplicitValueStubElement> {
    @NotNull
    LSFPropertyStatement getPropertyStatement();
}
