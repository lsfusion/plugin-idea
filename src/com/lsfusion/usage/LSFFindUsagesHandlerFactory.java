package com.lsfusion.usage;

import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesHandlerFactory;
import com.intellij.lang.findUsages.LanguageFindUsages;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFileSystemItem;
import com.lsfusion.lang.LSFLanguage;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFFindUsagesHandlerFactory extends FindUsagesHandlerFactory {
    @Override
    public boolean canFindUsages(@NotNull PsiElement element) {
        if (element instanceof PsiFileSystemItem) {
            if (((PsiFileSystemItem) element).getVirtualFile() == null) return false;
        } else if (!LanguageFindUsages.INSTANCE.forLanguage(element.getLanguage()).canFindUsagesFor(element)) {
            return false;
        }
        return element.isValid();
    }

    @Nullable
    @Override
    public FindUsagesHandler createFindUsagesHandler(@NotNull PsiElement element, boolean forHighlightUsages) {
        if (element.getLanguage() instanceof LSFLanguage && canFindUsages(element)) {
            return new LSFFindUsagesHandler(element) {
            };
        }
        return null;
    }
}
