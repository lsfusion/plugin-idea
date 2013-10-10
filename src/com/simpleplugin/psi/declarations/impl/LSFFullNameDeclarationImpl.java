package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.CollectionQuery;
import com.simpleplugin.psi.Finalizer;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.LSFSimpleNameWithCaption;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.stubs.FullNameStubElement;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public abstract class LSFFullNameDeclarationImpl<This extends LSFFullNameDeclaration<This,Stub>, Stub extends FullNameStubElement<Stub, This>> extends LSFGlobalDeclarationImpl<This, Stub> implements LSFFullNameDeclaration<This, Stub> {

    public LSFFullNameDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFFullNameDeclarationImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    protected static String calcGlobalName(LSFSimpleNameWithCaption caption) {
        return caption.getSimpleName().getText();
    }

    protected Collection<FullNameStubElementType> getTypes() {
        return Collections.singleton(getType());
    }

    protected FullNameStubElementType getType() {
        return null;
    }

    protected Condition<This> getFindDuplicatesCondition() {
        return Condition.TRUE;
    }

    @Override
    public String getNamespaceName() {
        return getLSFFile().getModuleDeclaration().getNamespace();
    }

    @Override
    public String getPresentableText() {
        return getDeclName();
    }

    @Override
    public boolean resolveDuplicates() {
        CollectionQuery<This> declarations = new CollectionQuery<This>(LSFGlobalResolver.findElements(getDeclName(), null, getLSFFile(), getTypes(), getFindDuplicatesCondition(), Finalizer.EMPTY));
        return declarations.findAll().size() > 1;
    }
}
