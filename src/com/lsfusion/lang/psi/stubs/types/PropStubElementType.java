package com.lsfusion.lang.psi.stubs.types;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubIndexKey;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFAggrParamGlobalPropDeclarationImpl;
import com.lsfusion.lang.psi.stubs.ActionOrPropStubElement;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import com.lsfusion.lang.psi.stubs.impl.PropStubImpl;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.PropIndex;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public abstract class PropStubElementType<StubT extends ActionOrPropStubElement<StubT, PsiT>, PsiT extends LSFActionOrGlobalPropDeclaration<PsiT, StubT>> extends ActionOrPropStubElementType<StubT, PsiT> {

    public PropStubElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public StringStubIndexExtension<PsiT> getGlobalIndex() {
        return BaseUtils.immutableCast(PropIndex.getInstance());
    }

    @Override
    public StubIndexKey<String, PsiT> getGlobalIndexKey() {
        return BaseUtils.immutableCast(LSFIndexKeys.PROP);
    }
}
