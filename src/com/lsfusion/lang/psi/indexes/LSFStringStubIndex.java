package com.lsfusion.lang.psi.indexes;

import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndex;
import com.lsfusion.lang.psi.LSFSourceFilterScope;
import com.lsfusion.lang.psi.LSFStubbedElement;

import java.util.Collection;

public abstract class LSFStringStubIndex<Psi extends LSFStubbedElement> extends StringStubIndexExtension<Psi> {
    @Override
    public Collection<Psi> get(String s, Project project, GlobalSearchScope scope) {
        return DumbService.getInstance(project).runReadActionInSmartMode(() -> StubIndex.getElements(getKey(), s, project, new LSFSourceFilterScope(scope), getPsiClass()));
    }
    
    protected abstract Class<Psi> getPsiClass();
}
