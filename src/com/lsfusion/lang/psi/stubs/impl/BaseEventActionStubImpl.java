package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.LSFImplicitExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFBaseEventActionDeclaration;
import com.lsfusion.lang.psi.stubs.BaseEventActionStubElement;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.util.Set;

public class BaseEventActionStubImpl extends ActStubImpl<BaseEventActionStubElement, LSFBaseEventActionDeclaration> implements BaseEventActionStubElement {

    public BaseEventActionStubImpl(StubElement parent, LSFBaseEventActionDeclaration psi) {
        super(parent, psi);
    }

    public BaseEventActionStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<BaseEventActionStubElement, LSFBaseEventActionDeclaration> type) throws IOException {
        super(dataStream, parentStub, type);
    }
}
