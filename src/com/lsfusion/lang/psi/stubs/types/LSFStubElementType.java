package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.LSFStubElement;
import com.lsfusion.lang.psi.LSFStubbedElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class LSFStubElementType<StubT extends LSFStubElement<StubT, PsiT>, PsiT extends LSFStubbedElement<PsiT, StubT>> extends IStubElementType<StubT, PsiT> {

    protected LSFStubElementType(@NotNull @NonNls String debugName) {
        super(debugName, LSFLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "lsf.stub." + this;
    }

    @Override
    public void serialize(@NotNull StubT stub, @NotNull StubOutputStream dataStream) throws IOException {
        stub.serialize(dataStream);
    }
}
