package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class PropertyComplexityCache extends PsiDependentCache<LSFPropDeclaration, Integer> {
    public static final PsiResolver<LSFPropDeclaration, Integer> RESOLVER = new PsiResolver<LSFPropDeclaration, Integer>() {
        @Override
        public Integer resolve(@NotNull LSFPropDeclaration lsfPropDeclaration, boolean incompleteCode) {
            return lsfPropDeclaration.getComplexity();
        }
    };

    public static PropertyComplexityCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, PropertyComplexityCache.class);
    }
    
    public PropertyComplexityCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    @Nullable
    public Integer resolveWithCaching(@NotNull LSFPropDeclaration element) {
        return super.resolveWithCaching(element, RESOLVER, false, false);
    }
}
