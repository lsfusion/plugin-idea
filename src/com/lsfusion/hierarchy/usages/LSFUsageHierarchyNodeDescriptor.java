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
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
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
        return myElement.isValid();
    }

    @Override
    public void navigate(boolean requestFocus) {
        ((NavigationItem) myElement).navigate(true);
    }

    @Override
    public boolean canNavigate() {
        return true;
    }

    @Override
    public boolean canNavigateToSource() {
        return true;
    }

    public PsiElement getNodeElement() {
        return myElement;
    }

    public PsiElement getElementId() {
        return nameIdentifier;
    }

    private LSFId getId() {
        if (myElement instanceof LSFFormStatement) {
            LSFFormDeclaration formDeclaration = ((LSFFormStatement) myElement).resolveFormDecl();
            return formDeclaration != null ? formDeclaration.getNameIdentifier() : null;
        } else if (myElement instanceof LSFOverrideStatement) {
            return ((LSFOverrideStatement) myElement).getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage().getCompoundID().getSimpleName();
        } else if (myElement instanceof LSFDesignStatement) {
            LSFFormDeclaration formDeclaration = ((LSFDesignStatement) myElement).resolveFormDecl();
            return formDeclaration != null ? formDeclaration.getNameIdentifier() : null;
        } else if (myElement instanceof LSFExplicitInterfacePropertyStatement) {
            return ((LSFExplicitInterfacePropertyStatement) myElement).getPropertyStatement().getNameIdentifier();
        } else if (myElement instanceof LSFClassStatement) {
            LSFClassDecl classDecl = ((LSFClassStatement) myElement).getClassDecl();
            return classDecl != null ? classDecl.getNameIdentifier() : null;
        }
        return null;
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LSFUsageHierarchyNodeDescriptor && myElement.equals(((LSFUsageHierarchyNodeDescriptor) obj).myElement);
    }
}
