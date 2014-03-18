package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubBase;
import com.intellij.psi.stubs.StubElement;
import com.lsfusion.lang.psi.declarations.LSFComponentStubDeclaration;
import com.lsfusion.lang.psi.stubs.ComponentStubElement;
import com.lsfusion.lang.psi.stubs.types.ComponentStubElementType;

public class ComponentStubImpl extends StubBase<LSFComponentStubDeclaration> implements ComponentStubElement {
    public String name;

    public ComponentStubImpl(StubElement parent, ComponentStubElementType elementType) {
        super(parent, elementType);
    }

    public ComponentStubImpl(StubElement parent, ComponentStubElementType elementType, String name) {
        this(parent, elementType);
        this.name = name;
    }
}
