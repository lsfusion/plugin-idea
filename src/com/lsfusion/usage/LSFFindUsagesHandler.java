package com.lsfusion.usage;

import com.intellij.find.findUsages.AbstractFindUsagesDialog;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiElement;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import com.lsfusion.ImplementationsSearch;
import com.lsfusion.lang.psi.LSFFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFFindUsagesHandler extends FindUsagesHandler {
    private final LSFFindUsagesOptions options;

    public LSFFindUsagesHandler(@NotNull PsiElement psiElement) {
        super(psiElement);
        options = new LSFFindUsagesOptions(getProject());
    }

    @NotNull
    @Override
    public AbstractFindUsagesDialog getFindUsagesDialog(boolean isSingleFile, boolean toShowInNewTab, boolean mustOpenInNewTab) {
        if (getPsiElement().getContainingFile() instanceof LSFFile) {
            return new LSFFindUsagesDialog(getPsiElement(), getProject(), options, toShowInNewTab, mustOpenInNewTab, isSingleFile, this);
        }
        return super.getFindUsagesDialog(isSingleFile, toShowInNewTab, mustOpenInNewTab);
    }

    @NotNull
    @Override
    public LSFFindUsagesOptions getFindUsagesOptions(@Nullable DataContext dataContext) {
        return options;
    }

    @Override
    public boolean processElementUsages(@NotNull final PsiElement element, @NotNull final Processor<UsageInfo> processor, @NotNull FindUsagesOptions options) {
        if (options.isUsages) {
            super.processElementUsages(element, processor, options);
        }
        if (((LSFFindUsagesOptions) options).isImplementingMethods) {
            ImplementationsSearch is = new ImplementationsSearch();
            is.processQuery(element, new Processor<PsiElement>() {
                @Override
                public boolean process(PsiElement psiElement) {
                    processor.process(new UsageInfo(psiElement.getParent()));
                    return true;
                }
            });
        }
        return true;
    }
}
