package com.lsfusion.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFGlobalResolver;
import com.lsfusion.lang.psi.LSFInterfacePropStatement;
import com.lsfusion.lang.psi.LSFLocalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.impl.LSFStatementActionDeclarationImpl;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Set;

public class LSFStructureTreeElementBase extends PsiTreeElementBase<PsiFile> {

    private final LSFValueClass valueClass;
    private final LSFStructureViewNavigationHandler navigationHandler;
    private final ActionOrPropType type;

    protected LSFStructureTreeElementBase(PsiFile file, LSFValueClass valueClass, LSFStructureViewNavigationHandler navigationHandler, ActionOrPropType type) {
        super(file);
        this.valueClass = valueClass;
        this.navigationHandler = navigationHandler;
        this.type = type;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        final ArrayList<StructureViewTreeElement> children = new ArrayList<>();

        if (valueClass != null && valueClass.isValid() && getElement() != null) {
            GlobalSearchScope scope = LSFGlobalResolver.getRequireScope((LSFFile) getElement());
            LSFLocalSearchScope localScope = LSFLocalSearchScope.GLOBAL;

            if(type.isProp()) {
                Set<LSFPropertyStatementTreeElement> propElements = LSFPsiUtils.mapPropertiesApplicableToClass(valueClass, getElement().getProject(), scope, localScope, new LSFPsiUtils.ApplicableMapper<>() {
                    @Override
                    public LSFPropertyStatementTreeElement map(LSFInterfacePropStatement statement, LSFValueClass valueClass) {
                        return statement instanceof LSFGlobalPropDeclaration ? new LSFPropertyStatementTreeElement(valueClass, ((LSFGlobalPropDeclaration) statement), navigationHandler) : null;
                    }
                }, true, true);
                propElements.remove(null);
                children.addAll(propElements);
            }
            if(type.isAction()) {
                Set<LSFActionStatementTreeElement> actionElements = LSFPsiUtils.mapActionsApplicableToClass(valueClass, getElement().getProject(), scope, localScope, new LSFPsiUtils.ApplicableMapper<>() {
                    @Override
                    public LSFActionStatementTreeElement map(LSFInterfacePropStatement statement, LSFValueClass valueClass) {
                        // the same check as the "has no return value" error for an action used in an expression
                        if (type.isValueActionsOnly() && !(statement instanceof LSFStatementActionDeclarationImpl && ((LSFStatementActionDeclarationImpl) statement).hasExplicitReturn())) {
                            return null;
                        }
                        return new LSFActionStatementTreeElement(valueClass, ((LSFActionDeclaration) statement), navigationHandler);
                    }
                }, true, true);
                actionElements.remove(null);
                children.addAll(actionElements);
            }
        }
        children.sort(Sorter.ALPHA_SORTER.getComparator());
        return children;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return null;
    }
}
