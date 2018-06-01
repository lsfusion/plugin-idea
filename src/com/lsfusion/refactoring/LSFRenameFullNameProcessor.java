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
import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropertyDrawDeclaration;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
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
        LSFDeclaration decl = PsiTreeUtil.getParentOfType(element, LSFDeclaration.class);
        if(decl instanceof LSFFullNameDeclaration)
            return (LSFFullNameDeclaration)decl;
        return null;
    }
    
    public static boolean isMigrationNeeded(PsiElement element) {
        return getMigration(element, "dumb") != null;
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
        cascadePostRenames = new ArrayList<>(); // just in case

        LSFPropertyDeclaration propDecl = PsiTreeUtil.getParentOfType(element, LSFPropertyDeclaration.class);
        if (propDecl != null) {
            //переименование свойства => нужно переименовать и соответствующие propertyDraw

            GlobalSearchScope scope = GlobalSearchScope.projectScope(element.getProject());
            Collection<PsiReference> refs = ReferencesSearch.search(element, scope, false).findAll();
            for (PsiReference ref : refs) {
                PsiElement refParent = PsiTreeUtil.getParentOfType(ref.getElement(), LSFPropertyDrawDeclaration.class, LSFFormPropertyOptionsList.class);
                if (refParent instanceof LSFPropertyDrawDeclaration) {
                    LSFPropertyDrawDeclaration propDrawDecl = (LSFPropertyDrawDeclaration)refParent;
                    //ищем propertyDraw без alias'а
                    if (propDrawDecl.getSimpleName() == null) {
                        LSFId propUsageId = getDeclPropName(propDrawDecl);
                        if(propUsageId != null) {
                            allRenames.put(propUsageId, newName);
                            cascadePostRenames.add(getMigrationRunnable(propUsageId, newName, MigrationChangePolicy.USE_LAST_VERSION));
                        }
                    }
                }
            }
        } else {
            LSFPropertyDrawDeclaration propDrawDecl = PsiTreeUtil.getParentOfType(element, LSFPropertyDrawDeclaration.class);
            if (propDrawDecl != null && propDrawDecl.getSimpleName() == null) {
                //переименование propertyDraw без alias => нужно переименовать и соответствующее свойство
                LSFFormPropertyName formPropertyName = propDrawDecl.getFormPropertyName();
                if(formPropertyName != null) {
                    LSFActionOrPropReference<?,?> propertyUsage = formPropertyName.getPropertyElseActionUsage();
                    if(propertyUsage == null)
                        propertyUsage = formPropertyName.getActionUsage();
                    if (propertyUsage != null) {
                        LSFActionOrPropDeclaration decl = propertyUsage.resolveDecl();
                        if (decl != null) {
                            // Делаем так, чтобы в allRenames сначало добавилось само свойство, а только потом соответствующее
                            // свойсnво на форме. Делается это для того, чтобы в миграционный скрипт эти переименования
                            // записались в одну версию
                            allRenames.remove(element);
                            allRenames.put(decl.getNameIdentifier(), newName);
                            allRenames.put(element, newName);
                            cascadePostRenames.add(getMigrationRunnable(element, newName, MigrationChangePolicy.USE_LAST_VERSION));
                        }
                    }
                }
            }
        }
    }

    public static LSFId getDeclPropName(LSFPropertyDrawDeclaration propDrawDecl) {
        LSFFormPropertyName formPropertyName = propDrawDecl.getFormPropertyName();
        if(formPropertyName == null)
            return null;

        LSFActionOrPropReference<?,?> propertyUsage = formPropertyName.getPropertyElseActionUsage();
        if(propertyUsage == null)
            propertyUsage = formPropertyName.getActionUsage();
        LSFId propUsageId = null;
        if (propertyUsage != null) {
            propUsageId = propertyUsage.getSimpleName();
        }
        return propUsageId;
    }

    @Override
    public void renameElement(PsiElement element, String newName, UsageInfo[] usages, @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
        LSFFullNameDeclaration decl = getFullNameDecl(element);
        if(decl != null) {
            List<Pair<LSFFullNameReference, LSFDeclaration>> possibleConflicts = new ArrayList<>();
            for (LSFFullNameReference possibleConflict : LSFResolver.findRenameConflicts(newName, decl)) {
                possibleConflicts.add(Pair.create(possibleConflict, possibleConflict.resolveDecl()));
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
    
    public static ElementMigration getMigration(PsiElement element, String newName) {
        LSFDeclaration propDecl = PsiTreeUtil.getParentOfType(element, LSFDeclaration.class);
        if(propDecl != null)
            return propDecl.getMigration(newName);
        return null;
    }
    
    // нужно заранее делать, потому как в getPostRenameCallback element'ы уже без контекста попадают
    private List<Runnable> cascadePostRenames = new ArrayList<>();  

    @Nullable
    @Override
    public Runnable getPostRenameCallback(final PsiElement element, String newName, RefactoringElementListener elementListener) {
        if (migrationPolicy != null) {
            final Runnable migrationRunnable = getMigrationRunnable(element, newName, migrationPolicy);
            if(!cascadePostRenames.isEmpty()) {
                final List<Runnable> fCascadePostRenames = cascadePostRenames;
                cascadePostRenames = new ArrayList<>();
                return new Runnable() {
                    public void run() {
                        if(migrationRunnable != null)
                            migrationRunnable.run();
                        
                        for(Runnable cascade : fCascadePostRenames)
                            cascade.run();
                    }
                };
            }
            return migrationRunnable;
        }
        return null;
    }

    public Runnable getMigrationRunnable(final PsiElement element, String newName, MigrationChangePolicy migrationPolicy) {
        final ElementMigration migration;
        if((migration = getMigration(element, newName)) != null) {
            final GlobalSearchScope scope = LSFFileUtils.getModuleWithDependantsScope(element);
            final Project project = element.getProject();
            return new Runnable() {
                @Override
                public void run() {
                    ShortenNamesProcessor.modifyMigrationScripts(Collections.singletonList(migration), migrationPolicy, project, scope);
                }
            };
        }
        return null;
    }

    private static void qualifyPossibleConflict(LSFFullNameReference ref, LSFDeclaration decl, MetaTransaction transaction) {
        if(ref.resolveDecl() == decl)
            return;
        
        // работаем везде на "горячем" PSI (то есть без копий, dummy и т.п.)

        if(ref instanceof LSFActionOrPropReference && ((LSFActionOrPropReference)ref).getExplicitClasses()==null) {
            LSFActionOrPropDeclaration propDecl = (LSFActionOrPropDeclaration) decl;
            if(propDecl == null)
                return;
            List<LSFClassSet> declClasses = propDecl.resolveParamClasses();
            if(declClasses != null)
                ((LSFActionOrPropReference)ref).setExplicitClasses(declClasses, transaction);

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
