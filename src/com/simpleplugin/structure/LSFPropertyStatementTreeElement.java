package com.simpleplugin.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.psi.PsiElement;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.LSFClassDecl;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.psi.declarations.LSFExplicitInterfacePropStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class LSFPropertyStatementTreeElement extends PsiTreeElementBase<PsiElement> {
    private LSFClassDecl targetClass;

    protected LSFPropertyStatementTreeElement(LSFClassDecl targetClass, PsiElement element) {
        super(element);

        this.targetClass = targetClass;
    }

    public LSFClassDecl getTargetClass() {
        return targetClass;
    }

    public String getClassName() {
        return targetClass.getName();
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return new ArrayList<StructureViewTreeElement>();
    }

    @Nullable
    @Override
    public String getPresentableText() {
        LSFPropertyStatement propertyStatement = ((LSFExplicitInterfacePropStatement) getElement()).getPropertyStatement();

        String text = propertyStatement.getPresentableText();

        String caption = propertyStatement.getCaption();
        if (caption != null) {
            text += " " + caption;
        }

        LSFClassSet valueClass = propertyStatement.resolveValueClass(false);
        if (!propertyStatement.isAction()) {
            text += ": " + valueClass;
        }

        return text;
    }

    @Override
    public void navigate(boolean requestFocus) {
        super.navigate(false);
    }

    @Override
    public boolean canNavigateToSource() { // автоматом при навигации по дереву
        return false;
    }
}
