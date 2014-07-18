package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.StubBasedPsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.LSFLanguage;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class LSFStubElementType<StubT extends StubElement<PsiT>, PsiT extends StubBasedPsiElement<StubT>> extends IStubElementType<StubT, PsiT> {

    protected LSFStubElementType(@NotNull @NonNls String debugName) {
        super(debugName, LSFLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "lsf." + toString();
    }
}
