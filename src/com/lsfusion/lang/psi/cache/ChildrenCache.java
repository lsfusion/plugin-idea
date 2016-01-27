package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

public class ChildrenCache extends PsiDependentCache<LSFClassDeclaration, Collection<LSFClassDeclaration>> {
    public static final PsiResolver<LSFClassDeclaration, Collection<LSFClassDeclaration>> RESOLVER = new PsiResolver<LSFClassDeclaration, Collection<LSFClassDeclaration>>() {
        @Override
        public Collection<LSFClassDeclaration> resolve(@NotNull LSFClassDeclaration lsfClassDeclaration, boolean incompleteCode) {
            return CustomClassSet.getChildrenAllNoCache(lsfClassDeclaration);
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
