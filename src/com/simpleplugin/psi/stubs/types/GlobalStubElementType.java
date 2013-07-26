package com.simpleplugin.psi.stubs.types;

import com.intellij.lang.Language;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.*;
import com.simpleplugin.LSFLanguage;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;
import com.simpleplugin.psi.stubs.GlobalStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;

public abstract class GlobalStubElementType<StubT extends GlobalStubElement<StubT, PsiT>, PsiT extends LSFGlobalDeclaration<PsiT, StubT>> extends IStubElementType<StubT, PsiT> {

    protected GlobalStubElementType(@NotNull @NonNls String debugName) {
        super(debugName, LSFLanguage.INSTANCE);

        key = StubIndexKey.createIndexKey(getExternalId());
    }

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
