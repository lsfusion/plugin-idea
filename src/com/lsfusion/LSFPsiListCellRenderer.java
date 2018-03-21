package com.lsfusion;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.LSFClassStatement;
import com.lsfusion.lang.psi.LSFFormStatement;
import com.lsfusion.lang.psi.LSFOverrideActionStatement;
import com.lsfusion.lang.psi.LSFOverridePropertyStatement;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LSFPsiListCellRenderer extends DefaultPsiElementCellRenderer {
    @Override
    public String getElementText(PsiElement element) {
        PsiElement parentElement = getParentElement(element);
        if (parentElement != null) {
            return getCutText(parentElement);
        }

        return super.getElementText(element);
    }
    
    @Nullable
    private PsiElement getParentElement(PsiElement baseElement) {
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

    @Override
    public String getContainerText(PsiElement element, String name) {
        return LSFPsiUtils.getLocationString(element);
    }

    private String getCutText(PsiElement element) {
        String text = element.getText();

        if (text != null && text.length() > 60) {
            text = text.substring(0, 60).trim() + "...";
        }
        return text;
    }

    @Override
    protected Icon getIcon(PsiElement element) {
        PsiElement parentElement = getParentElement(element);
        if (parentElement != null) {
            return parentElement.getIcon(0);
        }
        
        return super.getIcon(element);
    }
    
    
}
