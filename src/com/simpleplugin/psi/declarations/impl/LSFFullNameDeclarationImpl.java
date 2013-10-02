package com.simpleplugin.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFSimpleNameWithCaption;
import com.simpleplugin.psi.declarations.LSFFullNameDeclaration;
import com.simpleplugin.psi.stubs.FullNameStubElement;
import org.jetbrains.annotations.NotNull;

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

    @Override
    public String getNamespaceName() {
        return getLSFFile().getModuleDeclaration().getNamespace();
    }

    @Override
    public String getPresentableDeclText() {
        return getDeclName();
    }
}
