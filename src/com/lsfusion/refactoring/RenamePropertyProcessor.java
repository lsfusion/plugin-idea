package com.lsfusion.refactoring;

import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFPropertyDeclaration;
import com.lsfusion.lang.psi.LSFPropertyUsage;
import com.lsfusion.lang.psi.LSFSimpleName;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Map;

public class RenamePropertyProcessor extends RenamePsiElementProcessor {
    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return element instanceof LSFId;
    }

    @Override
    public void prepareRenaming(PsiElement element, String newName, Map<PsiElement, String> allRenames) {
        LSFPropertyDeclaration propDecl = PsiTreeUtil.getParentOfType(element, LSFPropertyDeclaration.class);
        if (propDecl != null) {
            //переименование свойства => нужно переименовать и соответствующие propertyDraw

            GlobalSearchScope scope = GlobalSearchScope.projectScope(element.getProject());
            Collection<PsiReference> refs = ReferencesSearch.search(element, scope, false).findAll();
            for (PsiReference ref : refs) {
                PsiElement refElement = ref.getElement();
                LSFPropertyDrawDeclaration propDrawDecl = PsiTreeUtil.getParentOfType(refElement, LSFPropertyDrawDeclaration.class);
                //ищем propertyDraw без alias'а
                if (propDrawDecl != null && propDrawDecl.getSimpleName() == null) {
                    LSFPropertyUsage propertyUsage = propDrawDecl.getFormPropertyName().getPropertyUsage();
                    if (propertyUsage != null) {
                        LSFSimpleName propUsageId = propertyUsage.getCompoundID().getSimpleName();
                        allRenames.put(propUsageId, newName);
                    }
                }
            }
        } else {
            LSFPropertyDrawDeclaration propDrawDecl = PsiTreeUtil.getParentOfType(element, LSFPropertyDrawDeclaration.class);
            if (propDrawDecl != null && propDrawDecl.getSimpleName() == null) {
                //переименование propertyDraw без alias => нужно переименовать и соответствующее свойство
                LSFPropertyUsage propertyUsage = propDrawDecl.getFormPropertyName().getPropertyUsage();
                if (propertyUsage != null) {
                    LSFPropDeclaration decl = propertyUsage.resolveDecl();
                    if (decl != null) {
                        allRenames.put(decl.getNameIdentifier(), newName);
                    }
                }
            }
        }
    }
}
