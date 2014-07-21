package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import org.jetbrains.annotations.NotNull;

public class ColumnNameCache extends PsiDependentCache<LSFGlobalPropDeclaration, String> {
    public static final PsiResolver<LSFGlobalPropDeclaration, String> RESOLVER = new PsiResolver<LSFGlobalPropDeclaration, String>() {
        @Override
        public String resolve(@NotNull LSFGlobalPropDeclaration globalProp, boolean incompleteCode) {
            return globalProp.getColumnNameNoCache();
        }
    };
    
    public static ColumnNameCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, ColumnNameCache.class);
    }

    public ColumnNameCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    public String getColumnNameWithCaching(LSFGlobalPropDeclaration element) {
        return resolveWithCaching(element, RESOLVER, true, false);
    }
}
