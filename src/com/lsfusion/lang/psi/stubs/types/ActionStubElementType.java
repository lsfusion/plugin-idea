package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.indexes.ActionIndex;
import com.lsfusion.lang.psi.indexes.LSFIndexKeys;
import com.lsfusion.lang.psi.indexes.LSFStringStubIndex;
import com.lsfusion.lang.psi.stubs.ActionOrPropStubElement;
import com.lsfusion.util.BaseUtils;

//по аналогии с PropStubElementStype
public abstract class ActionStubElementType<StubT extends ActionOrPropStubElement<StubT, PsiT>, PsiT extends LSFActionOrGlobalPropDeclaration<PsiT, StubT>> extends ActionOrPropStubElementType<StubT, PsiT> {

    public ActionStubElementType(String debugName) {
        super(debugName);
    }

    @Override
    public LSFStringStubIndex<PsiT> getGlobalIndex() {
        return BaseUtils.immutableCast(ActionIndex.getInstance());
    }

    @Override
    public StubIndexKey<String, PsiT> getGlobalIndexKey() {
        return BaseUtils.immutableCast(LSFIndexKeys.ACTION);
    }
}
