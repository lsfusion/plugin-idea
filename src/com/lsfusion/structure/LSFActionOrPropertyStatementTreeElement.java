package com.lsfusion.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.LSFPropertyStatement;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;

public abstract class LSFActionOrPropertyStatementTreeElement<T extends LSFActionOrPropDeclaration>  extends PsiTreeElementBase<T> {

    private final LSFValueClass paramClass;
    protected final LSFStructureViewNavigationHandler navigationHandler;

    public LSFActionOrPropertyStatementTreeElement(LSFValueClass paramClass, T element, LSFStructureViewNavigationHandler navigationHandler) {
        super(element);
        this.paramClass = paramClass;
        this.navigationHandler = navigationHandler;
    }

    public LSFValueClass getParamClass() {
        return paramClass;
    }

    public String getClassName() {
        return paramClass.getName();
    }

    @Override
    public boolean equals(Object o) {
        return super.equals(o) && paramClass == ((LSFActionOrPropertyStatementTreeElement) o).paramClass;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        return new ArrayList<>();
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
}
