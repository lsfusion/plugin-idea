package com.simpleplugin.psi.declarations.impl;

import com.intellij.icons.AllIcons;
import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.IStubElementType;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.LSFId;
import com.simpleplugin.psi.LSFSimpleNameWithCaption;
import com.simpleplugin.psi.declarations.LSFFormDeclaration;
import com.simpleplugin.psi.extend.LSFFormExtend;
import com.simpleplugin.psi.extend.impl.LSFFormExtendImpl;
import com.simpleplugin.psi.stubs.FormStubElement;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;
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
        return AllIcons.FileTypes.UiForm;
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
