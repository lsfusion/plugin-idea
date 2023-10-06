package com.lsfusion;

import com.intellij.codeInsight.daemon.LineMarkerInfo;
import com.intellij.codeInsight.daemon.LineMarkerProvider;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public abstract class LSFLineMarkerProvider implements LineMarkerProvider {
    @Override
    final public LineMarkerInfo<?> getLineMarkerInfo(@NotNull PsiElement element) {
        PsiFile containingFile = element.getContainingFile();
        if (InjectedLanguageManager.getInstance(element.getProject()).isInjectedFragment(containingFile)) {
            return null;
        }
        return getLSFLineMarkerInfo(element);
    }
    
    protected abstract LineMarkerInfo<?> getLSFLineMarkerInfo(@NotNull PsiElement element);
    
    @Override
    final public void collectSlowLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
        if (elements.isEmpty()) return;
        PsiFile containingFile = elements.get(0).getContainingFile();
        if (InjectedLanguageManager.getInstance(containingFile.getProject()).isInjectedFragment(containingFile)) {
            return;
        }
        collectSlowLSFLineMarkers(elements, result);
    }
    
    protected void collectSlowLSFLineMarkers(@NotNull List<? extends PsiElement> elements, @NotNull Collection<? super LineMarkerInfo<?>> result) {
    
    }
}
