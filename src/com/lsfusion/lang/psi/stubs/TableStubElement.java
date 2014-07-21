package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import org.jetbrains.annotations.NotNull;

public interface TableStubElement extends FullNameStubElement<TableStubElement, LSFTableDeclaration> {
    @NotNull
    String getClassNamesString();
}
