package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.ColumnNamingPolicy;
import com.lsfusion.lang.psi.LSFFile;
import org.jetbrains.annotations.NotNull;

public class ColumnNamingPolicyCache extends PsiDependentCache<LSFFile, ColumnNamingPolicy> {
    public static final PsiResolver<LSFFile, ColumnNamingPolicy> RESOLVER = new PsiResolver<LSFFile, ColumnNamingPolicy>() {
        @Override
        public ColumnNamingPolicy resolve(@NotNull LSFFile declaration, boolean incompleteCode) {
            return ColumnNamingPolicy.getInstance(declaration);
        }

        @Override
        public boolean checkResultClass(Object result) {
            return result instanceof ColumnNamingPolicy;
        }
    };

    public static ColumnNamingPolicyCache getInstance(LSFFile lsfFile) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(lsfFile.getProject(), ColumnNamingPolicyCache.class);
    }

    public ColumnNamingPolicyCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    public ColumnNamingPolicy getColumnNamingPolicyWithCaching(LSFFile element) {
        return resolveWithCaching(element, RESOLVER, true, false);
    }
}