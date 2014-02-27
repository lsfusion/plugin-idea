package com.lsfusion.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.ide.util.treeView.smartTree.Sorter;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.lsfusion.classes.LSFValueClass;
import com.lsfusion.psi.LSFFile;
import com.lsfusion.psi.LSFGlobalResolver;
import com.lsfusion.psi.LSFPropertyStatement;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

public class LSFStructureTreeElementBase extends PsiTreeElementBase<PsiFile> {

    private final LSFValueClass valueClass;
    private final LSFStructureViewNavigationHandler navigationHandler;

    protected LSFStructureTreeElementBase(PsiFile file, LSFValueClass valueClass, LSFStructureViewNavigationHandler navigationHandler) {
        super(file);
        this.valueClass = valueClass;
        this.navigationHandler = navigationHandler;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        final ArrayList<StructureViewTreeElement> children = new ArrayList<StructureViewTreeElement>();

        if (valueClass != null && getElement() != null) {
            GlobalSearchScope scope = LSFGlobalResolver.getRequireScope((LSFFile) getElement());

            for (LSFPropertyStatementTreeElement statement : LSFPsiUtils.getClassInterfaces(valueClass, getElement().getProject(), scope, new LSFPsiUtils.ResultHandler<LSFPropertyStatementTreeElement>() {
                @Override
                public LSFPropertyStatementTreeElement getResult(LSFPropertyStatement statement, LSFValueClass valueClass) {
                    return new LSFPropertyStatementTreeElement(valueClass, statement, navigationHandler);
                }
            })) {
                children.add(statement);
            }
        }
        Collections.sort(children, Sorter.ALPHA_SORTER.getComparator());
        return children;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return null;
    }
}
