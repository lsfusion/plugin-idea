package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.stubs.ActionOrPropStubElement;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.PropIndex;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

public abstract class PropStubElementType<StubT extends ActionOrPropStubElement<StubT, PsiT>, PsiT extends LSFActionOrGlobalPropDeclaration<PsiT, StubT>> extends ActionOrPropStubElementType<StubT, PsiT> {

    public PropStubElementType(@NotNull String debugName) {
        super(debugName);
    }

    @Override
    public LSFStringStubIndex<PsiT> getGlobalIndex() {
        return BaseUtils.immutableCast(PropIndex.getInstance());
    }

    @Override
    public StubIndexKey<String, PsiT> getGlobalIndexKey() {
        return BaseUtils.immutableCast(LSFIndexKeys.PROP);
    }
}
