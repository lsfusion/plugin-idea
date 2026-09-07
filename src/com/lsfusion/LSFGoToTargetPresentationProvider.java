package com.lsfusion;

import com.intellij.codeInsight.navigation.GotoTargetPresentationProvider;
import com.intellij.ide.util.PsiElementRenderingInfo;
import com.intellij.platform.backend.presentation.TargetPresentation;
import com.intellij.psi.PsiElement;
import com.intellij.psi.presentation.java.SymbolPresentationUtil;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.*;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LSFGoToTargetPresentationProvider implements GotoTargetPresentationProvider, PsiElementRenderingInfo<PsiElement> {
    @Nullable
    @Override
    public TargetPresentation getTargetPresentation(@NotNull PsiElement element, boolean differentNames) {
        if (element instanceof LSFElement || element instanceof LSFCustomClassUsageWrapper ||
                element instanceof LSFFormUsageWrapper || element instanceof LSFPropertyUsageWrapper || element instanceof LSFActionUsageWrapper) {
            return PsiElementRenderingInfo.targetPresentation(element, this);
        }
        return null;
    }

    @NotNull
    @Override
    public String getPresentableText(@NotNull PsiElement element) {
        PsiElement parentElement = getParentElement(element);
        if (parentElement != null) {
            String text = parentElement.getText();
            return text.length() > 60 ? text.substring(0, 60).trim() + "..." : text;
        }
        // the presentation needs a text, but declarations without a name identifier have no name
        String text = SymbolPresentationUtil.getSymbolPresentableText(element);
        return text != null ? text : element.getText();
    }

    @Nullable
    @Override
    public String getContainerText(@NotNull PsiElement element) {
        return LSFPsiUtils.getLocationString(element);
    }

    @Nullable
    @Override
    public Icon getIcon(@NotNull PsiElement element) {
        PsiElement parentElement = getParentElement(element);
        return (parentElement != null ? parentElement : element).getIcon(0);
    }

    @Nullable
    private static PsiElement getParentElement(PsiElement baseElement) {
        LSFOverridePropertyStatement overridePropertyStatement = PsiTreeUtil.getParentOfType(baseElement, LSFOverridePropertyStatement.class);
        if (overridePropertyStatement != null) {
            return overridePropertyStatement;
        }

        LSFOverrideActionStatement overrideActionStatement = PsiTreeUtil.getParentOfType(baseElement, LSFOverrideActionStatement.class);
        if (overrideActionStatement != null) {
            return overrideActionStatement;
        }

        LSFFormStatement formStatement = PsiTreeUtil.getParentOfType(baseElement, LSFFormStatement.class);
        if (formStatement != null) {
            return formStatement;
        }

        LSFClassStatement classStatement = PsiTreeUtil.getParentOfType(baseElement, LSFClassStatement.class);
        if (classStatement != null && classStatement.getExtendingClassDeclaration() != null) {
            return classStatement;
        }

        return null;
    }
}
