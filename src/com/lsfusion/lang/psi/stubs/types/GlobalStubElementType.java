package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.lang.psi.LSFGlobalElement;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class GlobalStubElementType<StubT extends GlobalStubElement<StubT, PsiT>, PsiT extends LSFGlobalElement<PsiT, StubT>> extends LSFStubElementType<StubT, PsiT> {
    protected GlobalStubElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }

    public abstract StringStubIndexExtension<PsiT> getGlobalIndex();

    public abstract StubIndexKey<String, PsiT> getGlobalIndexKey();

    @Override
    public void indexStub(@NotNull StubT stub, @NotNull IndexSink sink) {
        if(stub.getGlobalName() != null) {
            sink.occurrence(getGlobalIndexKey(), stub.getGlobalName());
        }
    }

    @Override
    public void serialize(@NotNull StubT stub, @NotNull StubOutputStream dataStream) throws IOException {
        stub.serialize(dataStream);
    }
}
