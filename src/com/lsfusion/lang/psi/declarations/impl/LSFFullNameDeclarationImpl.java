package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.util.CollectionQuery;
import com.lsfusion.lang.psi.Finalizer;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFSimpleNameWithCaption;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.stubs.FullNameStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;

public abstract class LSFFullNameDeclarationImpl<This extends LSFFullNameDeclaration<This, Stub>, Stub extends FullNameStubElement<Stub, This>> extends LSFGlobalDeclarationImpl<This, Stub> implements LSFFullNameDeclaration<This, Stub> {

    public LSFFullNameDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFFullNameDeclarationImpl(@NotNull Stub stub, @NotNull IStubElementType nodeType) {
        super(stub, nodeType);
    }

    protected static String calcGlobalName(LSFSimpleNameWithCaption caption) {
        return caption.getSimpleName().getText();
    }

    public Collection<FullNameStubElementType> getTypes() {
        return Collections.singleton(getType());
    }

    protected FullNameStubElementType getType() {
        return null;
    }

    protected Condition<This> getFindDuplicatesCondition() {
        return Conditions.alwaysTrue();
    }

    @Override
    public String getNamespaceName() {
        return getLSFFile().getModuleDeclaration().getNamespace();
    }

    public int getOffset() {
        Stub stub = getStub();
        if(stub != null)
            return stub.getOffset();

        return getTextOffset();
    }

    @Override
    public String getPresentableText() {
        return getDeclName();
    }

    @Override
    public boolean resolveDuplicates() {
        CollectionQuery<This> declarations = new CollectionQuery<>(LSFGlobalResolver.findElements(getDeclName(), null, getTypes(), getLSFFile(), null, getFindDuplicatesCondition(), Finalizer.EMPTY));
        return declarations.findAll().size() > 1;
    }

    public String getCanonicalName() {
        return getNamespaceName() + "." + getGlobalName();
    }
}
