package com.lsfusion.lang.psi.stubs.types;

import com.lsfusion.lang.psi.declarations.LSFNamespaceDeclaration;
import com.lsfusion.lang.psi.stubs.NamespaceStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class NamespaceStubElementType<StubT extends NamespaceStubElement<StubT, PsiT>, PsiT extends LSFNamespaceDeclaration<PsiT, StubT>> extends GlobalStubElementType<StubT, PsiT> {

    public NamespaceStubElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }
}
