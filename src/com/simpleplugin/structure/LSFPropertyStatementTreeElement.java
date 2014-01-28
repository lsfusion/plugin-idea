package com.simpleplugin.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.simpleplugin.classes.LSFValueClass;
import com.simpleplugin.psi.declarations.LSFExplicitInterfacePropStatement;
import com.simpleplugin.util.PsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class LSFPropertyStatementTreeElement extends PsiTreeElementBase<LSFExplicitInterfacePropStatement> {
    private final LSFStructureViewNavigationHandler navigationHandler;
    private final LSFValueClass valueClass;

    public LSFPropertyStatementTreeElement(@NotNull LSFValueClass valueClass, LSFExplicitInterfacePropStatement element, LSFStructureViewNavigationHandler navigationHandler) {
        super(element);
        this.valueClass = valueClass;
        this.navigationHandler = navigationHandler;
    }

    public LSFValueClass getValueClass() {
        return valueClass;
    }

    public String getClassName() {
        return valueClass.getName();
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return new ArrayList<StructureViewTreeElement>();
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return PsiUtils.getPropertyStatementPresentableText(getElement());
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (navigationHandler != null) {
            navigationHandler.navigate(this, requestFocus);
        }
    }

    @Override
    public boolean canNavigateToSource() { // автоматом при навигации по дереву
        return navigationHandler != null;
    }
}
