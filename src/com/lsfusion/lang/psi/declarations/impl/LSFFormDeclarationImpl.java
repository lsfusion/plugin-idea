package com.lsfusion.lang.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFLocalizedStringLiteral;
import com.lsfusion.lang.psi.LSFSimpleNameWithCaption;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.extend.impl.LSFFormExtendImpl;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.stubs.FormStubElement;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.List;

public abstract class LSFFormDeclarationImpl extends LSFFullNameDeclarationImpl<LSFFormDeclaration, FormStubElement> implements LSFFormDeclaration {

    public LSFFormDeclarationImpl(@NotNull ASTNode node) {
        super(node);
    }

    public LSFFormDeclarationImpl(@NotNull FormStubElement formStubElement, @NotNull IStubElementType nodeType) {
        super(formStubElement, nodeType);
    }

    protected abstract LSFSimpleNameWithCaption getSimpleNameWithCaption();

    @Override
    public LSFId getNameIdentifier() {
        return getSimpleNameWithCaption().getSimpleName();
    }

    @Override
    @NotNull
    public String getCaption() {
        LSFLocalizedStringLiteral stringLiteral = getSimpleNameWithCaption().getLocalizedStringLiteral();
        return stringLiteral == null ? "" : stringLiteral.getValue();
    }

    @Nullable
    @Override
    public Icon getIcon(int flags) {
        return LSFIcons.FORM;
    }

    @Override
    protected FullNameStubElementType getType() {
        return LSFStubElementTypes.FORM;
    }

    @Override
    public ExtendStubElementType<?, ?> getExtendElementType() {
        return LSFStubElementTypes.EXTENDFORM;
    }

    @Override
    public PsiElement[] processImplementationsSearch() {
        List<LSFFullNameReference> names = LSFFormExtendImpl.processFormImplementationsSearch(this);
        return names.toArray(new PsiElement[0]);
    }
}
