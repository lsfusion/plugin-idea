package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ChildrenCache extends PsiDependentCache<LSFClassDeclaration, Collection<LSFClassDeclaration>> {
    public static final PsiResolver<LSFClassDeclaration, Collection<LSFClassDeclaration>> RESOLVER = new PsiResolver<>() {
        @Override
        public Collection<LSFClassDeclaration> resolve(@NotNull LSFClassDeclaration lsfClassDeclaration, boolean incompleteCode) {
            return CustomClassSet.getChildrenAllNoCache(lsfClassDeclaration);
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

    public static ChildrenCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, ChildrenCache.class);
    }

    public ChildrenCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    public Collection<LSFClassDeclaration> getChildrenWithCaching(LSFClassDeclaration element) {
        return resolveWithCaching(element, RESOLVER, true, false);
    }
}
