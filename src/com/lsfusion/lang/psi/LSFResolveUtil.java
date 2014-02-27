package com.lsfusion.psi;

import com.intellij.openapi.util.Condition;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementResolveResult;
import com.intellij.psi.ResolveResult;
import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.psi.declarations.LSFDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.List;

public class LSFResolveUtil {

    @NotNull
    public static ResolveResult[] toCandidateInfoArray(@Nullable List<? extends PsiElement> elements) {
        if (elements == null) {
            return ResolveResult.EMPTY_ARRAY;
        }
        elements = ContainerUtil.filter(elements, new Condition<PsiElement>() {
            @Override
            public boolean value(PsiElement element) {
                return element != null;
            }
        });
        final ResolveResult[] result = new ResolveResult[elements.size()];
        for (int i = 0, size = elements.size(); i < size; i++) {
            result[i] = new PsiElementResolveResult(elements.get(i));
        }
        return result;
    }

    public static <T extends LSFDeclaration> T singleResolve(Collection<? extends LSFDeclaration> declarations) {
        if (declarations == null) {
            return null;
        }

        declarations = ContainerUtil.filter(declarations, new Condition<LSFDeclaration>() {
            @Override
            public boolean value(LSFDeclaration lsfDeclaration) {
                return lsfDeclaration != null;
            }
        });
        return (T) (declarations.size() != 1 ? null : declarations.iterator().next());
    }
}
