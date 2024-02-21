package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ParentsCache extends PsiDependentCache<LSFClassDeclaration, Collection<LSFClassDeclaration>> {
    public static final PsiResolver<LSFClassDeclaration, Collection<LSFClassDeclaration>> RESOLVER = new PsiResolver<>() {
        @Override
        public Collection<LSFClassDeclaration> resolve(@NotNull LSFClassDeclaration lsfClassDeclaration, boolean incompleteCode) {
            return CustomClassSet.getParentsNoCache(lsfClassDeclaration);
        }

        @Override
        public boolean checkResultClass(Object result) {
            if(!(result instanceof Collection))
                return false;

            for(Object element : (Collection)result)
                if(!(element instanceof LSFClassDeclaration))
                    return false;
            return true;

        }
    };

    public static ParentsCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, ParentsCache.class);
    }

    public ParentsCache(Project project) {
        super(project);
    }

    public Collection<LSFClassDeclaration> getParentsWithCaching(LSFClassDeclaration element) {
        return resolveWithCaching(element, RESOLVER, true, false);
    }
}
