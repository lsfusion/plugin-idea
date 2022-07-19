package com.lsfusion.lang.psi.cache;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.progress.ProgressIndicatorProvider;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.messages.MessageBus;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFModuleUsage;
import com.lsfusion.lang.psi.LSFRequireList;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashSet;
import java.util.Set;

public class ModuleDependentsCache extends PsiDependentCache<LSFModuleDeclaration, Set<LSFModuleDeclaration>> {
    public static ModuleDependentsCache getInstance(Project project) {
        ProgressIndicatorProvider.checkCanceled();
        return ServiceManager.getService(project, ModuleDependentsCache.class);
    }

    public static final PsiResolver<LSFModuleDeclaration, Set<LSFModuleDeclaration>> RESOLVER = new PsiResolver<>() {
        @Override
        public Set<LSFModuleDeclaration> resolve(@NotNull LSFModuleDeclaration declaration, boolean incompleteCode) {
            Set<LSFModuleDeclaration> result = new HashSet<>();
            LSFId nameIdentifier = declaration.getNameIdentifier();
            if (nameIdentifier != null) {
                ReferencesSearch.search(nameIdentifier, declaration.getUseScope()).forEach(reference -> {
                    if (reference instanceof LSFModuleUsage && PsiTreeUtil.getParentOfType((PsiElement) reference, LSFRequireList.class) != null) {
                        LSFModuleDeclaration decl = PsiTreeUtil.getParentOfType((PsiElement) reference, LSFModuleDeclaration.class);
                        if (decl != null) {
                            result.add(decl);
                        }
                    }
                });
            }
            return result;
        }

        @Override
        public boolean checkResultClass(Object result) {
            if (!(result instanceof Set)) {
                return false;
            }

            for (Object element : (Set) result) {
                if (!(element instanceof LSFModuleDeclaration)) {
                    return false;
                }
            }
            return true;
        }
    };

    public ModuleDependentsCache(@NotNull MessageBus messageBus) {
        super(messageBus);
    }

    @Nullable
    public Set<LSFModuleDeclaration> resolveWithCaching(@NotNull LSFModuleDeclaration element) {
        return super.resolveWithCaching(element, RESOLVER, false, false);
    }
}
