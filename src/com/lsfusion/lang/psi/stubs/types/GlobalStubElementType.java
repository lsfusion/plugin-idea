package com.lsfusion.psi.stubs.types;

import com.intellij.psi.stubs.*;
import com.lsfusion.LSFLanguage;
import com.lsfusion.psi.LSFGlobalElement;
import com.lsfusion.psi.stubs.GlobalStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class GlobalStubElementType<StubT extends GlobalStubElement<StubT, PsiT>, PsiT extends LSFGlobalElement<PsiT, StubT>> extends IStubElementType<StubT, PsiT> {
                                                                                                                        
    protected GlobalStubElementType(@NotNull @NonNls String debugName) {
        super(debugName, LSFLanguage.INSTANCE);

        key = StubIndexKey.createIndexKey(getExternalId());
    }

    @NotNull
    @Override
    public String getExternalId() {
        return "lsf." + toString();
    }

    public final StubIndexKey<String, PsiT> key;
    public abstract StringStubIndexExtension<PsiT> getGlobalIndex();

    @Override
    public void indexStub(StubT stub, IndexSink sink) {
        sink.occurrence(key, stub.getGlobalName());
    }

    @Override
    public void serialize(StubT stub, StubOutputStream dataStream) throws IOException {
        stub.serialize(dataStream);
    }
}
