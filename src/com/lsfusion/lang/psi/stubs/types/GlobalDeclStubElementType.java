package com.lsfusion.lang.psi.stubs.types;

import com.lsfusion.lang.psi.declarations.LSFGlobalDeclaration;
import com.lsfusion.lang.psi.stubs.GlobalDeclStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class GlobalDeclStubElementType<StubT extends GlobalDeclStubElement<StubT, PsiT>, PsiT extends LSFGlobalDeclaration<PsiT, StubT>>
        extends GlobalStubElementType<StubT, PsiT> {

    protected GlobalDeclStubElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }
}
