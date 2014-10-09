package com.lsfusion.lang.psi.indexes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;

import java.util.Collection;

public abstract class LSFStringStubIndex<Psi extends PsiElement> extends StringStubIndexExtension<Psi> {
    @Override
    public Collection<Psi> get(String s, Project project, GlobalSearchScope scope) {
        return StubIndex.getElements(getKey(), s, project, scope, getPsiClass());
    }
    
    protected abstract Class<Psi> getPsiClass();
}