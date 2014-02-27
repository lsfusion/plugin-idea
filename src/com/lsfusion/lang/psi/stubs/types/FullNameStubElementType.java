package com.lsfusion.psi.stubs.types;

import com.lsfusion.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.psi.stubs.FullNameStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class FullNameStubElementType<StubT extends FullNameStubElement<StubT, PsiT>, PsiT extends LSFFullNameDeclaration<PsiT, StubT>> extends GlobalStubElementType<StubT, PsiT>  {

    protected FullNameStubElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }
}
