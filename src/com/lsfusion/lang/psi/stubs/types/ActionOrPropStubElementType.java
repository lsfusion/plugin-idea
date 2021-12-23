package com.lsfusion.lang.psi.stubs.types;

import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.stubs.ActionOrPropStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class ActionOrPropStubElementType<StubT extends ActionOrPropStubElement<StubT, PsiT>, PsiT extends LSFActionOrGlobalPropDeclaration<PsiT, StubT>> extends FullNameStubElementType<StubT, PsiT> {
    
    public ActionOrPropStubElementType(@NotNull String debugName) {
        super(debugName);
    }
}
