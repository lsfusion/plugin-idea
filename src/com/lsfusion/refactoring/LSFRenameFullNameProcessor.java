package com.lsfusion.refactoring;

import com.intellij.notification.Notification;
import com.intellij.notification.NotificationType;
import com.intellij.notification.Notifications;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.refactoring.listeners.RefactoringElementListener;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.refactoring.rename.RenamePsiElementProcessor;
import com.intellij.usageView.UsageInfo;
import com.intellij.util.IncorrectOperationException;
import com.intellij.util.containers.MultiMap;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.references.LSFPropReference;
import com.lsfusion.util.LSFFileUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

public class LSFRenameFullNameProcessor extends RenamePsiElementProcessor {

    private MigrationChangePolicy migrationPolicy;

    @Override
    public boolean canProcessElement(@NotNull PsiElement element) {
        return element instanceof LSFId || getFullNameDecl(element) != null;
    }
    
    public static LSFFullNameDeclaration getFullNameDecl(PsiElement element) {
        return PsiTreeUtil.getParentOfType(element, LSFFullNameDeclaration.class);
    }
    
    public static boolean isStoredPropertyElement(PsiElement element) {
        LSFGlobalPropDeclaration propDecl = PsiTreeUtil.getParentOfType(element, LSFGlobalPropDeclaration.class);
        return propDecl != null && propDecl.isDataStoredProperty();
    }
    
/*    private static class PossibleConflict extends UsageInfo {
        private PossibleConflict(@NotNull PsiReference reference) {
            super(reference);
        }
    }

    @Override
    public void findCollisions(PsiElement element, String newName, Map<? extends PsiElement, String> allRenames, List<UsageInfo> result) {
        LSFFullNameDeclaration decl = getFullNameDecl(element);
        
        for(LSFFullNameReference possibleConflict : LSFResolver.findFullNameUsages(newName, decl))
            result.add(new PossibleConflict(possibleConflict));
    }*/

    public void setMigrationPolicy(MigrationChangePolicy migrationPolicy) {
        this.migrationPolicy = migrationPolicy;
    }

    @Override
    public RenameDialog createRenameDialog(Project project, PsiElement element, PsiElement nameSuggestionContext, Editor editor) {
        return new LSFRenameDialog(project, element, nameSuggestionContext, editor, this);
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

    @Override
    public void renameElement(PsiElement element, String newName, UsageInfo[] usages, @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
        LSFFullNameDeclaration decl = getFullNameDecl(element);
        if(decl != null) {
            List<Pair<LSFFullNameReference, LSFDeclaration>> possibleConflicts = new ArrayList<Pair<LSFFullNameReference, LSFDeclaration>>();
            for (LSFFullNameReference possibleConflict : LSFResolver.findFullNameUsages(newName, decl)) {
                possibleConflicts.add(new Pair<LSFFullNameReference, LSFDeclaration>(possibleConflict, possibleConflict.resolveDecl()));
            }

            for (UsageInfo usage : usages) {
                LSFFullNameReference reference = (LSFFullNameReference) usage.getReference();
                if (reference != null) {
//                if(usage instanceof PossibleConflict) {
//                    LSFDeclaration refDecl = reference.resolveDecl();
//                    possibleConflicts.add(new Pair<LSFFullNameReference, LSFDeclaration>(reference, refDecl));
//                } else
                    possibleConflicts.add(new Pair<LSFFullNameReference, LSFDeclaration>((LSFFullNameReference) reference.handleElementRename(newName), decl));
                }
            }

            decl.setName(newName);

            for (Pair<LSFFullNameReference, LSFDeclaration> possibleConflict : possibleConflicts)
                qualifyPossibleConflict(possibleConflict.first, possibleConflict.second, null);

            if (listener != null)
                listener.elementRenamed(decl);
        } else {
            super.renameElement(element, newName, usages, listener);
        }
    }

    @Nullable
    @Override
    public Runnable getPostRenameCallback(final PsiElement element, String newName, RefactoringElementListener elementListener) {
        LSFGlobalPropDeclaration propDecl = PsiTreeUtil.getParentOfType(element, LSFGlobalPropDeclaration.class);
        if (propDecl != null && propDecl.isDataStoredProperty() && migrationPolicy != null) {
            final PropertyMigration migration = new PropertyMigration(propDecl, propDecl.getGlobalName(), newName);
            final GlobalSearchScope scope = LSFFileUtils.getModuleWithDependantsScope(element);
            return new Runnable() {
                @Override
                public void run() {
                    ShortenNamesProcessor.modifyMigrationScripts(Collections.singletonList(migration), migrationPolicy, element.getProject(), scope);
                }
            };
        }
        return null;
    }

    private static void qualifyPossibleConflict(LSFFullNameReference ref, LSFDeclaration decl, MetaTransaction transaction) {
        if(ref.resolveDecl() == decl)
            return;
        
        // работаем везде на "горячем" PSI (то есть без копий, dummy и т.п.)

        if(ref instanceof LSFPropReference && ((LSFPropReference)ref).getExplicitClasses()==null) {
            LSFPropDeclaration propDecl = (LSFPropDeclaration) decl;
            List<LSFClassSet> declClasses = propDecl.resolveParamClasses();
            if(declClasses != null)
                ((LSFPropReference)ref).setExplicitClasses(declClasses, transaction);

            if(ref.resolveDecl() == decl)
                return;
        }

        if(decl instanceof LSFFullNameDeclaration && ref.getFullNameRef() == null) {
            ref.setFullNameRef(((LSFFullNameDeclaration)decl).getNamespaceName(), transaction);

            if(ref.resolveDecl() == decl)
                return;
        }

        Notifications.Bus.notify(new Notification("rename", "Rename", "Cannot qualify reference " + ref.getText() + " to declaration" + (decl == null ? "null" : decl.getText()), NotificationType.ERROR));
    }
    
    @Override
    public void findExistingNameConflicts(PsiElement element, String newName, MultiMap<PsiElement, String> conflicts) {
        // todo: implement
    }
}
