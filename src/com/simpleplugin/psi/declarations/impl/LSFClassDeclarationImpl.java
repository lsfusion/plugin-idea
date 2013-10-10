package com.simpleplugin.psi.declarations.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.LSFElementGenerator;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleNameWithCaption;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.references.LSFClassReference;
import com.simpleplugin.psi.stubs.ClassStubElement;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public abstract class LSFClassDeclarationImpl extends LSFFullNameDeclarationImpl<LSFClassDeclaration, ClassStubElement> implements LSFClassDeclaration {

    public LSFClassDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    protected LSFClassDeclarationImpl(@NotNull ClassStubElement classStubElement, @NotNull IStubElementType nodeType) {
        super(classStubElement, nodeType);
    }

    protected abstract LSFSimpleNameWithCaption getSimpleNameWithCaption();

    @Override
    public LSFId getNameIdentifier() {
        return getSimpleNameWithCaption().getSimpleName();
    }

    @Override
    public String getQName(PsiElement context) {
        String globalName = getGlobalName();
        LSFClassReference ref = LSFElementGenerator.createClassRefFromText(globalName, null, (LSFFile) context.getContainingFile());
        if(ref.resolveDecl() == this)
            return globalName;
        return getNamespaceName() + "." + globalName;
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return AllIcons.Nodes.Class;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.CLASS;
    }
}
