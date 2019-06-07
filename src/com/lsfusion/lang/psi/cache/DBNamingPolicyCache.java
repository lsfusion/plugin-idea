package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.DBNamingPolicy;
import com.lsfusion.lang.psi.LSFFile;
import org.jetbrains.annotations.NotNull;

public class DBNamingPolicyCache extends PsiDependentCache<LSFFile, DBNamingPolicy> {
    public static final PsiResolver<LSFFile, DBNamingPolicy> RESOLVER = new PsiResolver<LSFFile, DBNamingPolicy>() {
        @Override
        public DBNamingPolicy resolve(@NotNull LSFFile declaration, boolean incompleteCode) {
            return DBNamingPolicy.getInstance(declaration);
        }

        @Override
        public boolean checkResultClass(Object result) {
            return result instanceof DBNamingPolicy;
        }
    };

    public static DBNamingPolicyCache getInstance(LSFFile lsfFile) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(lsfFile.getProject(), DBNamingPolicyCache.class);
    }

    public DBNamingPolicyCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    public DBNamingPolicy getDBNamingPolicyWithCaching(LSFFile element) {
        return resolveWithCaching(element, RESOLVER, true, false);
    }
}