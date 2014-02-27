package com.lsfusion.psi.declarations.impl;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.lsfusion.LSFIcons;
import com.lsfusion.psi.LSFGlobalResolver;
import com.lsfusion.psi.LSFId;
import com.lsfusion.psi.LSFSimpleNameWithCaption;
import com.lsfusion.psi.declarations.LSFFormDeclaration;
import com.lsfusion.psi.extend.LSFFormExtend;
import com.lsfusion.psi.extend.impl.LSFFormExtendImpl;
import com.lsfusion.psi.stubs.FormStubElement;
import com.lsfusion.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collection;
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
    public PsiElement[] processExtensionsSearch() {
        Collection<LSFFormExtend> forms = LSFGlobalResolver.findExtendElements(this, LSFStubElementTypes.EXTENDFORM, getProject(), GlobalSearchScope.allScope(getProject())).findAll();
        List<PsiElement> names = new ArrayList<PsiElement>();
        for (LSFFormExtend extend : forms) {
            if (((LSFFormExtendImpl) extend).getFormDecl() == null) {
                names.add(((LSFFormExtendImpl) extend).getExtendingFormDeclaration().getFormUsage());
            }
        }
        return names.toArray(new PsiElement[names.size()]);
    }
}
