package com.simpleplugin.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.simpleplugin.classes.LSFValueClass;
import com.simpleplugin.psi.LSFPropertyStatement;
import com.simpleplugin.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public class LSFPropertyStatementTreeElement extends PsiTreeElementBase<LSFPropertyStatement> {
    private final LSFStructureViewNavigationHandler navigationHandler;
    private final LSFValueClass valueClass;

    public LSFPropertyStatementTreeElement(@NotNull LSFValueClass valueClass, LSFPropertyStatement element, LSFStructureViewNavigationHandler navigationHandler) {
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
        return LSFPsiUtils.getPropertyStatementPresentableText(getElement());
    }

    @Override
    public String getLocationString() {
        return getElement().getLSFFile().getModuleDeclaration().getNamespace();
    }

    @Override
    public void navigate(boolean requestFocus) {
        if (navigationHandler != null) {
            navigationHandler.navigate(this, requestFocus);
        } else {
            super.navigate(false);
        }
    }

    @Override
    public boolean canNavigateToSource() { // автоматом при навигации по дереву
        return navigationHandler != null;
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && valueClass == ((LSFPropertyStatementTreeElement) o).valueClass;
    }
}
