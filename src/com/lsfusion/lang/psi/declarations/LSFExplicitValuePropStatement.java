package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitValueStubElement;
import org.jetbrains.annotations.NotNull;

public interface LSFExplicitValuePropStatement extends LSFExplicitValueProp<ExplicitValueStubElement> {
    @NotNull
    LSFPropertyStatement getPropertyStatement();
}
