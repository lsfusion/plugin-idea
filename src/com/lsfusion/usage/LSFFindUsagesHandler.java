package com.lsfusion.usage;

import com.intellij.find.findUsages.AbstractFindUsagesDialog;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import com.lsfusion.ImplementationsSearch;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFFormObjectDeclaration;
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFParamDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import static com.lsfusion.actions.UsagesSearchAction.*;

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
            LSFDeclaration decl = PsiTreeUtil.getParentOfType(element, LSFDeclaration.class);
            if (decl instanceof LSFActionOrPropDeclaration && PROPERTY_DRAW_USAGES.equals(propertyUsagesSearchMode)) {
                // propertyDraw from formPropertyDraw declaration
                LSFPropertyDrawDeclaration propDrawDecl = PsiTreeUtil.getParentOfType(sourceElement, LSFPropertyDrawDeclaration.class);
                ReferencesSearch.search(propDrawDecl.getNameIdentifier()).forEach(psiReference -> {
                    processor.process(new UsageInfo(psiReference));
                    return true;
                });
            } else if (PARAMETER_USAGES.equals(propertyUsagesSearchMode)) {
                LSFParamDeclaration paramDecl = PsiTreeUtil.getParentOfType(sourceElement, LSFParamDeclaration.class);
                if (paramDecl != null && paramDecl.getNameIdentifier() != null) {
                    ReferencesSearch.search(paramDecl.getNameIdentifier()).forEach(psiReference -> {
                        processor.process(new UsageInfo(psiReference));
                        return true;
                    });
                }
            } else if (OBJECT_USAGES.equals(propertyUsagesSearchMode)) {
                LSFFormObjectDeclaration objectDecl = PsiTreeUtil.getParentOfType(element, LSFFormObjectDeclaration.class);
                if (objectDecl != null && objectDecl.getNameIdentifier() != null) {
                    super.processElementUsages(element, processor, options);
                }
            } else {
                super.processElementUsages(element, processor, options);
            }
        }
        if (((LSFFindUsagesOptions) options).isImplementingMethods) {
            ImplementationsSearch is = new ImplementationsSearch();
            is.processQuery(element, (Processor<PsiElement>) psiElement -> {
                processor.process(new UsageInfo(psiElement.getParent()));
                return true;
            });
        }
        return true;
    }
}
