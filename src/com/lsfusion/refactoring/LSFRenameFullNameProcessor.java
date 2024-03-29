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
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.references.LSFActionOrPropReference;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.util.LSFFileUtils;
import com.lsfusion.util.LSFPsiUtils;
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

    @NotNull
    @Override
    public RenameDialog createRenameDialog(@NotNull Project project, @NotNull PsiElement element, PsiElement nameSuggestionContext, Editor editor) {
        return new LSFRenameDialog(project, element, nameSuggestionContext, editor, this);
    }

    @Override
    public void prepareRenaming(@NotNull PsiElement element, @NotNull String newName, @NotNull Map<PsiElement, String> allRenames) {
        cascadePostRenameActions = new ArrayList<>(); // just in case
        cascadeRenameElements = new HashSet<>();
        
        if (PsiTreeUtil.getParentOfType(element, LSFClassDeclaration.class) != null) {
            prepareRenamingClass(element, newName);    
        } else if (PsiTreeUtil.getParentOfType(element, LSFPropertyDeclaration.class) != null) {
            // [todo] we need additional check that element is not a parameter name to prevent unwanted ref search
            prepareRenamingProperty(element, newName, allRenames);    
        } else if (PsiTreeUtil.getParentOfType(element, LSFPropertyDrawDeclaration.class) != null) {
            prepareRenamingPropertyDraw(element, newName, allRenames);
        }
    }
    
    private void prepareRenamingClass(@NotNull PsiElement element, String newName) {
        LSFClassDeclaration cls = PsiTreeUtil.getParentOfType(element, LSFClassDeclaration.class);
        if (cls != null && cls.isValid()) {
            Project project = cls.getProject();
            final ArrayList<LSFActionOrGlobalPropDeclaration<?, ?>> children = new ArrayList<>();
            GlobalSearchScope scope = GlobalSearchScope.allScope(project);
            LSFLocalSearchScope localScope = LSFLocalSearchScope.GLOBAL;
            children.addAll(LSFPsiUtils.mapPropertiesWithClassInSignature(cls, project, scope, localScope, LSFPsiUtils.ApplicableMapper.STATEMENT, true, true));
            children.addAll(LSFPsiUtils.mapActionsWithClassInSignature(cls, project, scope, localScope, LSFPsiUtils.ApplicableMapper.STATEMENT, true, true));

            children.sort(Comparator.comparing(LSFFullNameDeclaration::getCanonicalName));
            
            Map<GlobalSearchScope, List<LSFActionOrGlobalPropDeclaration<?, ?>>> groupMap = new HashMap<>();
            Map<LSFActionOrGlobalPropDeclaration<?, ?>, String> oldCanonicalNames = new HashMap<>();
            for (LSFActionOrGlobalPropDeclaration<?, ?> child : children) {
                cascadeRenameElements.add(child);
                oldCanonicalNames.put(child, child.getCanonicalName());
                final GlobalSearchScope childScope = LSFFileUtils.getModuleWithDependantsScope(child);
                if (!groupMap.containsKey(childScope)) {
                    groupMap.put(childScope, new ArrayList<>());
                }
                groupMap.get(childScope).add(child);
            }

            for (GlobalSearchScope gscope : groupMap.keySet()) {
                cascadePostRenameActions.add(getMigrationClassRunnable(groupMap.get(gscope), oldCanonicalNames, MigrationChangePolicy.USE_LAST_VERSION, gscope));
            }

            for (LSFClassExtend extend : LSFGlobalResolver.findParentExtends(cls)) {
                for (LSFStaticObjectDeclaration staticDecl : extend.getStaticObjects()) {
                    cascadeRenameElements.add(staticDecl);
                    cascadePostRenameActions.add(getMigrationClassRunnable(staticDecl, cls.getNamespaceName(), cls.getName(), newName, MigrationChangePolicy.USE_LAST_VERSION));
                }
            }
        }        
    }
    
    private void prepareRenamingProperty(@NotNull PsiElement element, @NotNull String newName, @NotNull Map<PsiElement, String> allRenames) {
        allRenames.remove(element);
        addPropertyDrawsToRenames(element, newName, allRenames);
        allRenames.put(element, newName);
    }
    
    private void prepareRenamingPropertyDraw(@NotNull PsiElement element, @NotNull String newName, @NotNull Map<PsiElement, String> allRenames) {
        LSFPropertyDrawDeclaration propDrawDecl = PsiTreeUtil.getParentOfType(element, LSFPropertyDrawDeclaration.class);
        if (propDrawDecl != null && propDrawDecl.getSimpleName() == null) {
            // when renaming form property without alias we need to rename property itself
            LSFFormPropertyName formPropertyName = propDrawDecl.getFormPropertyName();
            if (formPropertyName != null) {
                LSFActionOrPropReference<?, ?> propertyUsage = formPropertyName.getPropertyElseActionUsage();
                if (propertyUsage == null)
                    propertyUsage = formPropertyName.getActionUsage();
                if (propertyUsage != null) {
                    LSFActionOrPropDeclaration decl = propertyUsage.resolveDecl();
                    if (decl != null && decl.getNameIdentifier() != null) {
                        addPropertyDrawsToRenames(decl.getNameIdentifier(), newName, allRenames);
                        allRenames.put(decl.getNameIdentifier(), newName);
                    }
                }
            }
        }
    }
    
    private void addPropertyDrawsToRenames(@NotNull PsiElement element, @NotNull String newName, @NotNull Map<PsiElement, String> allRenames) {
        GlobalSearchScope scope = GlobalSearchScope.projectScope(element.getProject());
        Collection<PsiReference> refs = ReferencesSearch.search(element, scope, false).findAll();
        for (PsiReference ref : refs) {
            PsiElement refParent = PsiTreeUtil.getParentOfType(ref.getElement(), LSFPropertyDrawDeclaration.class, LSFFormPropertyOptionsList.class);
            if (refParent instanceof LSFPropertyDrawDeclaration) {
                LSFPropertyDrawDeclaration propDrawDecl = (LSFPropertyDrawDeclaration)refParent;
                // we need only form properties without declared alias 
                if (propDrawDecl.getSimpleName() == null) {
                    LSFId propUsageId = getDeclPropName(propDrawDecl);
                    if (propUsageId != null) {
                        allRenames.put(propUsageId, newName);
                        cascadePostRenameActions.add(getMigrationRunnable(propUsageId, newName, MigrationChangePolicy.USE_LAST_VERSION));
                        cascadeRenameElements.add(propUsageId);
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
    public void renameElement(@NotNull PsiElement element, @NotNull String newName, @NotNull UsageInfo[] usages, @Nullable RefactoringElementListener listener) throws IncorrectOperationException {
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
                    possibleConflicts.add(new Pair<>((LSFFullNameReference) reference.handleElementRename(newName), decl));
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
        LSFDeclaration decl = PsiTreeUtil.getParentOfType(element, LSFDeclaration.class);
        if(decl != null)
            return decl.getMigration(newName);
        return null;
    }
    
    // We need this container to get all rename lines in one block of migration file 
    private List<Runnable> cascadePostRenameActions = new ArrayList<>();
    private Set<PsiElement> cascadeRenameElements = new HashSet<>();

    @Nullable
    @Override
    public Runnable getPostRenameCallback(@NotNull final PsiElement element, @NotNull String newName, @NotNull RefactoringElementListener elementListener) {
        if (migrationPolicy != null && !cascadeRenameElements.contains(element)) {
            final Runnable migrationRunnable = getMigrationRunnable(element, newName, migrationPolicy);
            if (!cascadePostRenameActions.isEmpty()) {
                final List<Runnable> fCascadePostRenames = cascadePostRenameActions;
                cascadePostRenameActions = new ArrayList<>();
                cascadeRenameElements = new HashSet<>();
                return () -> {
                    if (migrationRunnable != null)
                        migrationRunnable.run();
                    
                    for (Runnable cascade : fCascadePostRenames)
                        cascade.run();
                };
            }
            return migrationRunnable;
        }
        return null;
    }

    public Runnable getMigrationRunnable(final PsiElement element, String newName, MigrationChangePolicy migrationPolicy) {
        final ElementMigration migration = getMigration(element, newName);
        if (migration != null) {
            final GlobalSearchScope scope = LSFFileUtils.getModuleWithDependantsScope(element);
            return () -> MigrationScriptUtils.modifyMigrationScripts(Collections.singletonList(migration), migrationPolicy, scope);
        }
        return null;
    }

    public Runnable getMigrationClassRunnable(final List<LSFActionOrGlobalPropDeclaration<?, ?>> decls, 
                                              final Map<LSFActionOrGlobalPropDeclaration<?, ?>, String> oldCanonicalNames, 
                                              MigrationChangePolicy migrationPolicy, GlobalSearchScope scope) {
        return () -> {
            List<ElementMigration> migrations = new ArrayList<>();
            for (LSFActionOrGlobalPropDeclaration<?, ?> decl : decls) {
                migrations.add(new PropertyMigration(decl, oldCanonicalNames.get(decl), decl.getCanonicalName(), true));                            
            }
            MigrationScriptUtils.modifyMigrationScripts(migrations, migrationPolicy, scope);
        };
    }

    public Runnable getMigrationClassRunnable(final LSFStaticObjectDeclaration decl, String namespace, String oldClassName, String newClassName, MigrationChangePolicy migrationPolicy) {
        StaticObjectMigration migration = new StaticObjectMigration(decl, namespace, oldClassName, newClassName, decl.getName(), decl.getName());
        final GlobalSearchScope scope = LSFFileUtils.getModuleWithDependantsScope(decl);
        return () -> MigrationScriptUtils.modifyMigrationScripts(Collections.singletonList(migration), migrationPolicy, scope);
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
    public void findExistingNameConflicts(@NotNull PsiElement element, @NotNull String newName, @NotNull MultiMap<PsiElement, String> conflicts) {
        // todo: implement
    }
}
