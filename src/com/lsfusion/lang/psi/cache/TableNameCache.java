package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import org.jetbrains.annotations.NotNull;

public class TableNameCache extends PsiDependentCache<LSFGlobalPropDeclaration, String> {
    public static final PsiResolver<LSFGlobalPropDeclaration, String> RESOLVER = new PsiResolver<LSFGlobalPropDeclaration, String>() {
        @Override
        public String resolve(@NotNull LSFGlobalPropDeclaration globalProp, boolean incompleteCode) {
            return globalProp.getTableNameNoCache();
        }

        @Override
        public boolean checkResultClass(Object result) {
            return result instanceof String;
        }
    };
    
    public static TableNameCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, TableNameCache.class);
    }

    public TableNameCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    public String getTableNameWithCaching(LSFGlobalPropDeclaration element) {
        return resolveWithCaching(element, RESOLVER, true, false);
    }
}
