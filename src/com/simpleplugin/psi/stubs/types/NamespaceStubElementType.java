package com.simpleplugin.psi.stubs.types;

import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;
import com.simpleplugin.psi.declarations.LSFNamespaceDeclaration;
import com.simpleplugin.psi.stubs.GlobalStubElement;
import com.simpleplugin.psi.stubs.NamespaceStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class NamespaceStubElementType<StubT extends NamespaceStubElement<StubT, PsiT>, PsiT extends LSFNamespaceDeclaration<PsiT, StubT>> extends GlobalStubElementType<StubT, PsiT> {

    public NamespaceStubElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }
}
