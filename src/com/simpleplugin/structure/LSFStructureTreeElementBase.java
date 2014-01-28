package com.simpleplugin.structure;

import com.intellij.ide.structureView.StructureViewTreeElement;
import com.intellij.ide.structureView.impl.common.PsiTreeElementBase;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.simpleplugin.classes.CustomClassSet;
import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.classes.LSFValueClass;
import com.simpleplugin.psi.declarations.LSFExplicitInterfacePropStatement;
import com.simpleplugin.psi.stubs.types.indexes.ExplicitInterfaceIndex;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LSFStructureTreeElementBase extends PsiTreeElementBase<PsiFile> {

    private final LSFValueClass valueClass;
    private final LSFStructureViewNavigationHandler navigationHandler;

    private List<StructureViewTreeElement> children;

    protected LSFStructureTreeElementBase(PsiFile file, LSFValueClass valueClass, LSFStructureViewNavigationHandler navigationHandler) {
        super(file);
        this.valueClass = valueClass;
        this.navigationHandler = navigationHandler;
    }

    @NotNull
    @Override
    public Collection<StructureViewTreeElement> getChildrenBase() {
        if (children == null) {
            if (valueClass != null) {
                children = new ArrayList<StructureViewTreeElement>();
                for (LSFValueClass vc : CustomClassSet.getClassParentsRecursively(valueClass)) {
                    children.addAll(getPropertyTreeElements(vc));
                }
            } else {
                children = Collections.emptyList();
            }
        }

        return children;
    }

    private Collection<LSFPropertyStatementTreeElement> getPropertyTreeElements(LSFValueClass valueClass) {
        if (getElement() == null || valueClass == null) {
            return Collections.emptyList();
        }

        String name = valueClass.getName();
        Project project = getElement().getProject();
        Collection<LSFExplicitInterfacePropStatement> statements = ExplicitInterfaceIndex.getInstance().get(name, project, GlobalSearchScope.allScope(project));

        List<LSFPropertyStatementTreeElement> result = new ArrayList<LSFPropertyStatementTreeElement>();

        for (LSFExplicitInterfacePropStatement statement : statements) {
            List<LSFClassSet> statementClasses = statement.getPropertyStatement().resolveParamClasses();
            if (statementClasses != null) {
                for (LSFClassSet classSet : statementClasses) {
                    if (classSet != null) {
                        LSFValueClass paramClass = classSet.getCommonClass();
                        if (valueClass.equals(paramClass)) {
                            result.add(new LSFPropertyStatementTreeElement(valueClass, statement, navigationHandler));
                            break;
                        }
                    }
                }
            }
        }
        return result;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return null;
    }
}
