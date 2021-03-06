package com.lsfusion.hierarchy.usages;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionOrPropStatement;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LSFUsageHierarchyNodeDescriptor extends HierarchyNodeDescriptor implements Navigatable {
    private LSFId nameIdentifier;

    protected LSFUsageHierarchyNodeDescriptor(@NotNull Project project, NodeDescriptor parentDescriptor, @NotNull PsiElement element, boolean isBase) {
        super(project, parentDescriptor, element, isBase);

        nameIdentifier = getId();

        String presentableText = element.getText();

        if (presentableText != null) {
            if (presentableText.length() > 60) {
                presentableText = presentableText.substring(0, 60).trim() + "...";
            }
            myHighlightedText.getBeginning().addText(presentableText);
            myName = presentableText;
        }
        myHighlightedText.getEnding().addText(" (" + ((LSFFile) element.getContainingFile()).getModuleDeclaration().getName() + ")", new TextAttributes(JBColor.GRAY, null, null, null, Font.PLAIN));
    }

    @Override
    public boolean isValid() {
        PsiElement psiElement = getPsiElement();
        if(psiElement == null)
            return false;

        return psiElement.isValid();
    }

    @Override
    public void navigate(boolean requestFocus) {
        ((NavigationItem) getPsiElement()).navigate(true);
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    public PsiElement getElementId() {
        return nameIdentifier;
    }

    private LSFId getId() {
        PsiElement myElement = getPsiElement();
        if (myElement instanceof LSFFormStatement) {
            LSFFormDeclaration formDeclaration = ((LSFFormStatement) myElement).resolveFormDecl();
            return formDeclaration != null ? formDeclaration.getNameIdentifier() : null;
        } else if (myElement instanceof LSFOverridePropertyStatement) {
            return ((LSFOverridePropertyStatement) myElement).getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage().getCompoundID().getSimpleName();
        } else if (myElement instanceof LSFOverrideActionStatement) {
            return ((LSFOverrideActionStatement) myElement).getMappedActionClassParamDeclare().getActionUsageWrapper().getActionUsage().getCompoundID().getSimpleName();
        } else if (myElement instanceof LSFDesignStatement) {
            LSFFormDeclaration formDeclaration = ((LSFDesignStatement) myElement).resolveFormDecl();
            return formDeclaration != null ? formDeclaration.getNameIdentifier() : null;
        } else if (myElement instanceof LSFExplicitInterfaceActionOrPropStatement) {
            return ((LSFExplicitInterfaceActionOrPropStatement) myElement).getDeclaration().getNameIdentifier();
        } else if (myElement instanceof LSFClassStatement) {
            LSFClassDecl classDecl = ((LSFClassStatement) myElement).getClassDecl();
            return classDecl != null ? classDecl.getNameIdentifier() : null;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LSFUsageHierarchyNodeDescriptor && BaseUtils.nullEquals(getPsiElement(),((LSFUsageHierarchyNodeDescriptor) obj).getPsiElement());
    }
}
