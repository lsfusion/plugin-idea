package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public class PropertyDependentsCache extends PsiDependentCache<LSFPropDeclaration, Set<LSFPropDeclaration>> {
    public static final PsiResolver<LSFPropDeclaration, Set<LSFPropDeclaration>> RESOLVER = new PsiResolver<LSFPropDeclaration, Set<LSFPropDeclaration>>() {
        @Override
        public Set<LSFPropDeclaration> resolve(@NotNull LSFPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.getDependents();
        }
    };

    public static PropertyDependentsCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, PropertyDependentsCache.class);
    }
    
    public PropertyDependentsCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    @Nullable
    public Set<LSFPropDeclaration> resolveWithCaching(@NotNull LSFPropDeclaration element) {
        return super.resolveWithCaching(element, RESOLVER, false, false);
    }
}
