package com.lsfusion.hierarchy.classes;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.ui.JBColor;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LSFClassHierarchyNodeDescriptor extends HierarchyNodeDescriptor implements Navigatable {
    protected LSFClassHierarchyNodeDescriptor(@NotNull Project project, NodeDescriptor parentDescriptor, @NotNull LSFClassDeclaration element, boolean isBase) {
        super(project, parentDescriptor, element, isBase);

        String presentableText = element.getDeclName();
        myHighlightedText.getBeginning().addText(presentableText);
        myHighlightedText.getEnding().addText(" (" + element.getLSFFile().getModuleDeclaration().getName() + ")", new TextAttributes(JBColor.GRAY, null, null, null, Font.PLAIN));

        myName = presentableText;
    }

    @Override
    public boolean isValid() {
        PsiElement psiElement = getPsiElement();
        return psiElement != null && psiElement.isValid();
    }

    @Override
    public void navigate(boolean requestFocus) {
        PsiElement psiElement = getPsiElement();
        if (psiElement != null) {
            ((NavigationItem) psiElement).navigate(true);
        }
    }

    @Override
    public boolean canNavigate() {
        PsiElement psiElement = getPsiElement();
        return psiElement != null && ((NavigationItem) psiElement).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        PsiElement psiElement = getPsiElement();
        return psiElement != null && ((NavigationItem) psiElement).canNavigateToSource();
    }

    public LSFClassDeclaration getClassDecl() {
        return (LSFClassDeclaration) getPsiElement();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LSFClassHierarchyNodeDescriptor && BaseUtils.nullEquals(getPsiElement(), ((LSFClassHierarchyNodeDescriptor) obj).getPsiElement());
    }
}
