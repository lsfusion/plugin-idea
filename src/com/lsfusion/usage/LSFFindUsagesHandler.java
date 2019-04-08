package com.lsfusion.usage;

import com.intellij.find.findUsages.AbstractFindUsagesDialog;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.Processor;
import com.lsfusion.ImplementationsSearch;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFFormObjectDeclaration;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFNewNavigatorElementStatement;
import com.lsfusion.lang.psi.declarations.*;
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
                referencesSearch(processor, propDrawDecl);
            } else if (PARAMETER_USAGES.equals(propertyUsagesSearchMode)) {
                LSFParamDeclaration paramDecl = PsiTreeUtil.getParentOfType(sourceElement, LSFParamDeclaration.class);
                referencesSearch(processor, paramDecl);
            } else if (OBJECT_USAGES.equals(propertyUsagesSearchMode)) {
                LSFFormObjectDeclaration objectDecl = PsiTreeUtil.getParentOfType(element, LSFFormObjectDeclaration.class);
                if (objectDecl != null && objectDecl.getNameIdentifier() != null) {
                    super.processElementUsages(element, processor, options);
                }
            } else if (FORM_USAGES.equals(propertyUsagesSearchMode)) {
                LSFDeclaration formDecl = PsiTreeUtil.getParentOfType(element, LSFFormDeclaration.class);
                referencesSearch(processor, formDecl);
            } else if (NAVIGATOR_ELEMENT_USAGES.equals(propertyUsagesSearchMode)) {
                ApplicationManager.getApplication().runReadAction(() -> {
                    LSFNavigatorElementDeclaration navigatorElementDecl = PsiTreeUtil.getParentOfType(element, LSFNavigatorElementDeclaration.class); //move
                    if(navigatorElementDecl == null) {
                        navigatorElementDecl = PsiTreeUtil.getParentOfType(sourceElement, LSFNewNavigatorElementStatement.class); //new
                    }
                    referencesSearch(processor, navigatorElementDecl);
                });
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

    private void referencesSearch(Processor<UsageInfo> processor, LSFDeclaration declaration) {
        if(declaration != null) {
            LSFId identifier = declaration.getNameIdentifier();
            if (identifier != null) {
                ReferencesSearch.search(identifier).forEach(psiReference -> {
                    processor.process(new UsageInfo(psiReference));
                    return true;
                });
            }
        }
    }
}
