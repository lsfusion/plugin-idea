package com.simpleplugin.psi.stubs.types;

import com.intellij.lang.Language;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.declarations.LSFGlobalDeclaration;
import com.simpleplugin.psi.stubs.FullNameStubElement;
import com.simpleplugin.psi.stubs.GlobalStubElement;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FullNameStubElementType<StubT extends FullNameStubElement<StubT, PsiT>, PsiT extends LSFFullNameDeclaration<PsiT, StubT>> extends GlobalStubElementType<StubT, PsiT>  {

    protected FullNameStubElementType(@NotNull @NonNls String debugName) {
        super(debugName);
    }
}
