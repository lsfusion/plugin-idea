package com.lsfusion.hierarchy.classes;

import com.intellij.ide.hierarchy.HierarchyNodeDescriptor;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
import com.intellij.ui.JBColor;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.awt.*;

public class LSFClassHierarchyNodeDescriptor extends HierarchyNodeDescriptor implements Navigatable {
    // Own pointer to the represented element: SmartElementDescriptor.getPsiElement() is platform-internal.
    private final SmartPsiElementPointer<PsiElement> targetPointer;

    protected LSFClassHierarchyNodeDescriptor(@NotNull Project project, NodeDescriptor parentDescriptor, @NotNull LSFClassDeclaration element, boolean isBase) {
        super(project, parentDescriptor, element, isBase);

        targetPointer = SmartPointerManager.getInstance(project).createSmartPsiElementPointer(element);

        String presentableText = element.getDeclName();
        myHighlightedText.getBeginning().addText(presentableText);
        myHighlightedText.getEnding().addText(" (" + element.getLSFFile().getModuleDeclaration().getName() + ")", new TextAttributes(JBColor.GRAY, null, null, null, Font.PLAIN));

        myName = presentableText;
    }

    /** Replacement for the platform-internal {@code SmartElementDescriptor.getPsiElement()}. */
    public @Nullable PsiElement getTargetElement() {
        return targetPointer.getElement();
    }

    @Override
    public boolean isValid() {
        PsiElement psiElement = getTargetElement();
        return psiElement != null && psiElement.isValid();
    }

    @Override
    public void navigate(boolean requestFocus) {
        PsiElement psiElement = getTargetElement();
        if (psiElement != null) {
            ((NavigationItem) psiElement).navigate(true);
        }
    }

    @Override
    public boolean canNavigate() {
        PsiElement psiElement = getTargetElement();
        return psiElement != null && ((NavigationItem) psiElement).canNavigate();
    }

    @Override
    public boolean canNavigateToSource() {
        PsiElement psiElement = getTargetElement();
        return psiElement != null && ((NavigationItem) psiElement).canNavigateToSource();
    }

    public LSFClassDeclaration getClassDecl() {
        return (LSFClassDeclaration) getTargetElement();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof LSFClassHierarchyNodeDescriptor && BaseUtils.nullEquals(getTargetElement(), ((LSFClassHierarchyNodeDescriptor) obj).getTargetElement());
    }
}
