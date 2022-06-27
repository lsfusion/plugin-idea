package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class PropertyDependenciesCache extends PsiDependentCache<LSFActionOrGlobalPropDeclaration<?, ?>, Set<LSFActionOrGlobalPropDeclaration<?, ?>>> {
    public static final PsiResolver<LSFActionOrGlobalPropDeclaration<?, ?>, Set<LSFActionOrGlobalPropDeclaration<?, ?>>> RESOLVER = new PsiResolver<>() {
        @Override
        public Set<LSFActionOrGlobalPropDeclaration<?, ?>> resolve(@NotNull LSFActionOrGlobalPropDeclaration<?, ?> lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.getDependencies();
        }

        @Override
        public boolean checkResultClass(Object result) {
            if(!(result instanceof Set))
                return false;

            for(Object element : (Set)result)
                if(!(element instanceof LSFActionOrGlobalPropDeclaration))
                    return false;
            return true;
        }
    };

    public static PropertyDependenciesCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, PropertyDependenciesCache.class);
    }
    
    public PropertyDependenciesCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    @Nullable
    public Set<LSFActionOrGlobalPropDeclaration<?, ?>> resolveWithCaching(@NotNull LSFActionOrGlobalPropDeclaration<?, ?> element) {
        return super.resolveWithCaching(element, RESOLVER, false, false);
    }
}
