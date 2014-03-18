package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.extapi.psi.StubBasedPsiElementBase;
import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.lang.psi.declarations.LSFComponentStubDeclaration;
import com.lsfusion.lang.psi.stubs.ComponentStubElement;
import org.jetbrains.annotations.NotNull;

public abstract class LSFComponentStubDeclarationImpl extends StubBasedPsiElementBase<ComponentStubElement> implements LSFComponentStubDeclaration {
    public LSFComponentStubDeclarationImpl(@NotNull ComponentStubElement stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    public LSFComponentStubDeclarationImpl(ASTNode node) {
        super(node);
    }
}
