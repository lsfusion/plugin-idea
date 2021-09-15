package com.lsfusion.hierarchy.classes;

import com.intellij.ide.hierarchy.*;
import com.intellij.ide.util.treeView.AlphaComparator;
import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.LSFClassStatement;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.references.LSFClassReference;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Comparator;
import java.util.Map;

public class LSFClassHierarchyBrowser extends TypeHierarchyBrowserBase {
    private static final Logger LOG = Logger.getInstance("#com.lsfusion.hierarchy.usages.LSFClassHierarchyBrowser");

    public LSFClassHierarchyBrowser(Project project, PsiElement element) {
        super(project, element);
    }

    @Override
    protected void prependActions(DefaultActionGroup actionGroup) {
        super.prependActions(actionGroup);

        AnAction toRemove = null;
        for (AnAction action : actionGroup.getChildActionsOrStubs()) {
            if (action instanceof ViewClassHierarchyAction) {
                toRemove = action;
                break;
            }
        }

        if (toRemove != null) {
            actionGroup.remove(toRemove);
        }
    }

    @Override
    protected boolean isInterface(PsiElement psiElement) {
        return false;
    }

    @Override
    protected boolean canBeDeleted(PsiElement psiElement) {
        return false;
    }

    @Override
    protected String getQualifiedName(PsiElement psiElement) {
        return null;
    }

    @Nullable
    @Override
    protected PsiElement getElementFromDescriptor(@NotNull HierarchyNodeDescriptor descriptor) {
        return null;
    }

    @Override
    protected void createTrees(@NotNull Map<? super String, ? super JTree> trees) {
        trees.put(TypeHierarchyBrowserBase.SUBTYPES_HIERARCHY_TYPE, createTree(false));
        trees.put(TypeHierarchyBrowserBase.SUPERTYPES_HIERARCHY_TYPE, createTree(false));
    }

    @Nullable
    @Override
    protected JPanel createLegendPanel() {
        return null;
    }

    @Override
    protected boolean isApplicableElement(@NotNull PsiElement element) {
        LSFClassDeclaration classDecl = findClassDecl(element);
        return classDecl != null && LSFPsiUtils.getStatementParent(element) instanceof LSFClassStatement;
    }

    private LSFClassDeclaration findClassDecl(PsiElement target) {
        PsiElement element = PsiTreeUtil.getParentOfType(target, LSFClassDeclaration.class, LSFClassReference.class);
        if (element instanceof LSFClassReference) {
            return ((LSFClassReference) element).resolveDecl();
        } else {
            return (LSFClassDeclaration) element;
        }
    }

    @Nullable
    @Override
    protected HierarchyTreeStructure createHierarchyTreeStructure(@NotNull String type, @NotNull PsiElement psiElement) {
        if (TypeHierarchyBrowserBase.SUPERTYPES_HIERARCHY_TYPE.equals(type)) {
            return new LSFSuperclassHierarchyTreeStructure(myProject, findClassDecl(psiElement));
        } else if (TypeHierarchyBrowserBase.SUBTYPES_HIERARCHY_TYPE.equals(type)) {
            return new LSFSubclassHierarchyTreeStructure(myProject, findClassDecl(psiElement));
        } else {
            LOG.error("unexpected type: " + type);
            return null;
        }
    }

    @Nullable
    @Override
    protected Comparator<NodeDescriptor<?>> getComparator() {
        if (HierarchyBrowserManager.getInstance(myProject).getState().SORT_ALPHABETICALLY) {
            return AlphaComparator.INSTANCE;
        }
        return null;
    }
}
