package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.IndexSink;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.LSFGlobalElement;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.stubs.GlobalStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class GlobalStubElementType<StubT extends GlobalStubElement<StubT, PsiT>, PsiT extends LSFGlobalElement<PsiT, StubT>> extends LSFStubElementType<StubT, PsiT> {
    protected GlobalStubElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }

    public abstract LSFStringStubIndex<PsiT> getGlobalIndex();

    public abstract StubIndexKey<String, PsiT> getGlobalIndexKey();

    @Override
    public void indexStub(@NotNull StubT stub, @NotNull IndexSink sink) {
        if(stub.getGlobalName() != null) {
            sink.occurrence(getGlobalIndexKey(), stub.getGlobalName());
        }
    }
}
