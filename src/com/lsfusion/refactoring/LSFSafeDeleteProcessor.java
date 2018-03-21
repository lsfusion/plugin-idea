package com.lsfusion.refactoring;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.safeDelete.NonCodeUsageSearchInfo;
import com.intellij.refactoring.safeDelete.SafeDeleteProcessorDelegateBase;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.CommonProcessors;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.usage.LSFFindUsagesHandler;
import com.lsfusion.usage.LSFFindUsagesOptions;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

public class LSFSafeDeleteProcessor extends SafeDeleteProcessorDelegateBase {
    @Nullable
    @Override
    public Collection<? extends PsiElement> getElementsToSearch(PsiElement element, @Nullable Module module, Collection<PsiElement> allElementsToDelete) {
        return Arrays.asList(getElementToDelete(element));
    }

    @Override
    public boolean handlesElement(PsiElement element) {
        return element.getLanguage().is(LSFLanguage.INSTANCE) && (!(element instanceof LSFFile) || ((LSFFile) element).getModuleDeclaration() != null);
    }

    @Nullable
    @Override
    public NonCodeUsageSearchInfo findUsages(PsiElement element, PsiElement[] allElementsToDelete, List<UsageInfo> result) {
            Project project = element.getProject();
            element = getNameIdentifier(element);
        new LSFFindUsagesHandler(element).processElementUsages(element, new CommonProcessors.CollectProcessor<>(result), new LSFFindUsagesOptions(project));
        return null;
    }

    @Nullable
    @Override
    public Collection<PsiElement> getAdditionalElementsToDelete(PsiElement element, Collection<PsiElement> allElementsToDelete, boolean askUser) {
        return null;
    }

    @Nullable
    @Override
    public Collection<String> findConflicts(PsiElement element, PsiElement[] allElementsToDelete) {
            List<UsageInfo> usages = new ArrayList<>();
            findUsages(element, allElementsToDelete, usages);
            if (!usages.isEmpty()) {
                return Arrays.asList("Element '" + getNameIdentifier(element).getText() +  "' has some usages.");
            }
        return null;
    }

    @Nullable
    @Override
    public UsageInfo[] preprocessUsages(Project project, UsageInfo[] usages) {
        return new UsageInfo[0];
    }
    
    private LSFId getNameIdentifier(PsiElement element) {
        if (element instanceof LSFClassStatement) {
            return ((LSFClassStatement) element).getClassDecl().getNameIdentifier();
        } else if (element instanceof LSFFormStatement) {
            return ((LSFFormStatement) element).getFormDecl().getNameIdentifier();
        } else if (element instanceof LSFWindowStatement) {
            return ((LSFWindowStatement) element).getWindowCreateStatement().getNameIdentifier();
        } else if (element instanceof LSFExplicitInterfaceActionOrPropStatement) {
            return ((LSFExplicitInterfaceActionOrPropStatement) element).getDeclaration().getNameIdentifier();
        } else if (element instanceof LSFNewComponentStatement) {
            return ((LSFNewComponentStatement) element).getComponentStubDecl().getComponentDecl().getNameIdentifier();
        } else if (element instanceof LSFFile) {
            LSFModuleDeclaration moduleDeclaration = ((LSFFile) element).getModuleDeclaration();
            return moduleDeclaration.getNameIdentifier();
        } else {
            return ((LSFDeclaration) element).getNameIdentifier();
        }
    }
    
    public static PsiElement getElementToDelete(PsiElement element) {
        if (element instanceof LSFFile) {
            return element;
        }
        PsiElement parentOfType = PsiTreeUtil.getParentOfType(element, LSFExplicitInterfacePropertyStatement.class, LSFClassStatement.class, LSFTableStatement.class,
                LSFFormStatement.class, LSFGroupStatement.class, LSFWindowStatement.class, LSFMetaCodeDeclarationStatement.class,
                
                LSFNewComponentStatement.class, LSFNavigatorElementDeclaration.class, LSFGroupObjectDeclaration.class,
                LSFPropertyDrawMappedDeclaration.class, LSFPropertyDrawNameDeclaration.class, LSFStaticObjectDecl.class,
                
                LSFParamDeclaration.class, LSFFilterGroupDeclaration.class);
        
        if (parentOfType instanceof LSFParamDeclaration || parentOfType instanceof LSFFilterGroupDeclaration) {
            return null;
        }
        return parentOfType;
    }

    @Override
    public void prepareForDeletion(PsiElement element) throws IncorrectOperationException {
    }

    @Override
    public boolean isToSearchInComments(PsiElement element) {
        return false;
    }

    @Override
    public void setToSearchInComments(PsiElement element, boolean enabled) {

    }

    @Override
    public boolean isToSearchForTextOccurrences(PsiElement element) {
        return false;
    }

    @Override
    public void setToSearchForTextOccurrences(PsiElement element, boolean enabled) {

    }
}
