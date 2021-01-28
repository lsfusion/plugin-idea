package com.lsfusion.lang.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Conditions;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.util.*;
import com.intellij.util.containers.ContainerUtil;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.cache.RequireModulesCache;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.indexes.ClassExtendsClassIndex;
import com.lsfusion.lang.psi.indexes.ExplicitNamespaceIndex;
import com.lsfusion.lang.psi.indexes.ModuleIndex;
import com.lsfusion.lang.psi.indexes.TableClassesIndex;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import com.lsfusion.lang.psi.stubs.FullNameStubElement;
import com.lsfusion.lang.psi.stubs.PropStubElement;
import com.lsfusion.lang.psi.stubs.extend.ExtendStubElement;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendStubElementType;
import com.lsfusion.lang.psi.stubs.types.FullNameStubElementType;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class LSFGlobalResolver {

    // тут можно использовать или RequireModulesCache (но проблема в том, что тогда он будет очищаться каждый раз и заново будет делать resolveDecl, с другой стороны там все за stub'но, так что не сильно большой оверхед)
    // или ручной кэш, он очищается только при изменении структуры модулей
    // пока попробуем автоматический
    public static Set<VirtualFile> getRequireModules(LSFModuleDeclaration declaration) {
//        return getCachedRequireModules(declaration);
        return getManualCachedRequireModules(declaration, new HashSet<VirtualFile>());
    }
    private static Set<VirtualFile> getCachedRequireModules(LSFModuleDeclaration declaration) {
        String name = declaration.getGlobalName();
        boolean toCache = name != null && !name.equals(LSFElementGenerator.genName) && !LSFPsiUtils.isInjected(declaration);
        if (toCache) {
            return RequireModulesCache.getInstance(declaration.getProject()).getRequireModulesWithCaching(declaration);
        }
        return getRequireModulesNoCache(declaration);
    }

    public static Set<VirtualFile> getRequireModulesNoCache(LSFModuleDeclaration declaration) {
        Set<VirtualFile> result = new HashSet<>();
        result.add(declaration.getLSFFile().getVirtualFile());
        for(LSFModuleDeclaration decl : declaration.getRequireModules())
            if(decl != null)
                result.addAll(getCachedRequireModules(decl));
        return result;
    }

    public static Map<LSFModuleDeclaration, Set<VirtualFile>> cached = ContainerUtil.createConcurrentWeakKeySoftValueMap();

    private static Set<VirtualFile> getManualCachedRequireModules(LSFModuleDeclaration declaration, Set<VirtualFile> alreadyGet) {
        String name = declaration.getGlobalName();
        boolean toCache = name != null && !name.equals(LSFElementGenerator.genName) && !LSFPsiUtils.isInjected(declaration);
        if (toCache) {
            Set<VirtualFile> cachedFiles = cached.get(declaration);
            if (cachedFiles != null)
                return cachedFiles;
        }

        Set<VirtualFile> result = new HashSet<>();

        VirtualFile declarationFile = declaration.getLSFFile().getVirtualFile();
        if (!alreadyGet.contains(declarationFile)) {
            result.add(declarationFile);
            alreadyGet.add(declarationFile);
            for (LSFModuleDeclaration decl : declaration.getRequireModules()) {
                Set<VirtualFile> requireModules = getManualCachedRequireModules(decl, alreadyGet);

                result.addAll(requireModules);
            }
//        System.out.println("CACHED "+declaration.getName()+" "+System.identityHashCode(declaration));
            if (toCache)
                cached.put(declaration, result);
        }
        return result;
    }

    public static GlobalSearchScope getRequireScope(LSFElement lsfElement) {
        return getRequireScope(lsfElement.getLSFFile());
    }

    public static GlobalSearchScope getRequireScope(LSFFile lsfFile) {
        if (lsfFile instanceof LSFCodeFragment && lsfFile.getContext() != null) {
            PsiFile containingFile = lsfFile.getContext().getContainingFile();
            if (containingFile instanceof LSFFile && containingFile != lsfFile) {
                return getRequireScope((LSFFile)containingFile);
            }
        }

        Project project = lsfFile.getProject();
        LSFModuleDeclaration declaration = lsfFile.getModuleDeclaration();
        VirtualFile vfile = lsfFile.getVirtualFile();
        if (vfile == null && declaration != null) {
            Query<LSFModuleDeclaration> modules = findModules(declaration.getGlobalName(), GlobalSearchScope.allScope(project));
            LSFModuleDeclaration first = modules.findFirst();
            if (first != null)
                declaration = first;
        }

        Set<VirtualFile> vFiles = new HashSet<>();
        if (declaration != null) {
            for (VirtualFile f : getRequireModules(declaration)) {
                if (f != null) {
                    vFiles.add(f); // null может быть только для dumb
                }
            }
        }
        return GlobalSearchScope.filesScope(project, vFiles);
    }

    private static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Collection<T> findInNamespace(Collection<T> decls) {
        return decls == null || decls.isEmpty() ? null : decls;
    }
    public static <S extends FullNameStubElement, T extends LSFFullNameDeclaration, SC extends FullNameStubElement<SC, TC>, TC extends LSFFullNameDeclaration<TC, SC>> Collection<T> findElements(String name, String fqName, Collection<FullNameStubElementType> types, LSFFile file, Integer offset, LSFLocalSearchScope localScope, Condition<T> condition, Finalizer<T> finalizer) {
        return findElements(name, fqName, types, file, offset, localScope, condition, finalizer, new ArrayList<>());
    }
    public static <S extends FullNameStubElement, T extends LSFFullNameDeclaration, SC extends FullNameStubElement<SC, TC>, TC extends LSFFullNameDeclaration<TC, SC>> Collection<T> findElements(String name, String fqName, Collection<FullNameStubElementType> types, LSFFile file, Integer offset, LSFLocalSearchScope localScope, Condition<T> condition, Finalizer<T> finalizer, List<T> virtDecls) {
        Condition<TC> conditionC = BaseUtils.immutableCast(condition);
        Finalizer<TC> finalizerC = BaseUtils.immutableCast(finalizer);
        List<TC> virtDeclsC = BaseUtils.immutableCast((Object)virtDecls);
        Collection<FullNameStubElementType<SC, TC>> fullNameStubElementTypes = BaseUtils.<Collection<FullNameStubElementType<SC, TC>>>immutableCast(types);
        return BaseUtils.<Collection<T>>immutableCast(findElements(name, fqName, file, offset, localScope, fullNameStubElementTypes, conditionC, finalizerC, virtDeclsC));
    }

    public static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> T findSystemElement(String name, LSFFile file, FullNameStubElementType<S, T> type) {
        Collection<T> system = findElements(name, "System", file, null, LSFLocalSearchScope.GLOBAL, Collections.singleton(type), Conditions.alwaysTrue());
        if(!system.isEmpty())
            return system.iterator().next();
        return null;
    }

    public static <G extends PsiElement> Collection<G> getItemsFromIndex(StringStubIndexExtension<G> index, String name, Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope) {
        return index.get(name, project, scope);
    }

    public static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Collection<T> findElements(String name, final String fqName, LSFFile file, Integer offset, LSFLocalSearchScope localScope, Collection<? extends FullNameStubElementType<S, T>> types, Condition<T> condition) {
        return findElements(name, fqName, file, offset, localScope, types, condition, new ArrayList<>());
    }

    public static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Collection<T> findElements(String name, final String fqName, LSFFile file, Integer offset, LSFLocalSearchScope localScope, Collection<? extends FullNameStubElementType<S, T>> types, Condition<T> condition, List<T> virtDecls) {
        return findElements(name, fqName, file, offset, localScope, types, condition, Finalizer.EMPTY, virtDecls);
    }

    public static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Collection<T> findElements(String name, final String fqName, LSFFile file, Integer offset, LSFLocalSearchScope localScope, Collection<? extends FullNameStubElementType<S, T>> types, Condition<T> condition, Finalizer<T> finalizer, List<T> virtDecls) {
        return findElements(name, fqName, file.getProject(), file.getRequireScope(), file, offset, localScope, types, condition, finalizer, virtDecls);
    }

    // globalscope (without any particular lsf module, fqname should be provided)
    public static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Collection<T> findElements(String name, final String fqName, Project project, GlobalSearchScope scope, FullNameStubElementType<S, T> types) {
        assert fqName != null;
        return findElements(name, fqName, project, scope, null, null, LSFLocalSearchScope.GLOBAL, Collections.singleton(types), Conditions.alwaysTrue(), Finalizer.EMPTY, Collections.emptyList());
    }

    public static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Collection<T> findElements(String name, final String fqName, Project project, GlobalSearchScope scope, LSFFile file, Integer offset, LSFLocalSearchScope localScope, Collection<? extends FullNameStubElementType<S, T>> types, Condition<T> condition, Finalizer<T> finalizer, List<T> virtDecls) {
        if (fqName != null) {
            final Condition<T> fCondition = condition;
            condition = t -> t.getNamespaceName().equals(fqName) && fCondition.value(t);
        } else {
            LSFModuleDeclaration moduleDeclaration = file.getModuleDeclaration();
            if(moduleDeclaration != null)
                finalizer = new NamespaceFinalizer<>(moduleDeclaration, finalizer);
        }

        Collection<T> decls = new ArrayList<>();
        Set<StringStubIndexExtension> usedIndices = new HashSet<>();
        for (FullNameStubElementType<S, T> type : types) {
            StringStubIndexExtension<T> index = type.getGlobalIndex();
            if(usedIndices.add(index))
                decls.addAll(getItemsFromIndex(index, name, project, scope, localScope));
        }
        for (T virtDecl : virtDecls) {
            if (virtDecl != null && name != null && name.equals(virtDecl.getDeclName())) {
                VirtualFile virtualFile = virtDecl.getLSFFile().getVirtualFile();
                if (virtualFile == null || scope.contains(virtualFile)) {
                    decls.add(virtDecl);
                }
            }
        }

        Collection<T> fitDecls = new ArrayList<>();
        for (T decl : decls)
            if (condition.value(decl))
                if (offset == null || !isAfter(file, offset, decl))
                    fitDecls.add(decl);

        return finalizer.finalize(fitDecls);
    }

    public static <This extends LSFGlobalPropDeclaration<This,Stub>, Stub extends PropStubElement<Stub, This>> LSFTableDeclaration findAppropriateTable(@NotNull LSFValueClass[] currentSet, LSFFile file) {
        String names[] = new String[currentSet.length];
        for (int i = 0; i < currentSet.length; i++) {
            names[i] = currentSet[i].getName();
        }

        String key = BaseUtils.toString(names);

        Collection<LSFTableDeclaration> tables = LSFGlobalResolver.getItemsFromIndex(TableClassesIndex.getInstance(), key, file.getProject(), file.getRequireScope(), LSFLocalSearchScope.GLOBAL);
        for (LSFTableDeclaration table : tables) {
            LSFValueClass[] tableClasses = table.getClasses();

            if (tableClasses.length != currentSet.length) {
                continue;
            }

            boolean fit = true;
            for (int i = 0; i < currentSet.length; ++i) {
                if (tableClasses[i] != currentSet[i]) {
                    fit = false;
                    break;
                }
            }

            if (fit) {
                return table;
            }
        }

        return null;
    }

    private static class NamespaceFinalizer<T extends LSFFullNameDeclaration> implements Finalizer<T> {

        private final LSFModuleDeclaration moduleDeclaration;
        private final Finalizer<T> finalizer;

        private NamespaceFinalizer(LSFModuleDeclaration moduleDeclaration, Finalizer<T> finalizer) {
            this.moduleDeclaration = moduleDeclaration;
            this.finalizer = finalizer;
        }

        public Collection<T> finalize(Collection<T> decls) {
            if (decls.size() == 1) // оптимизация
                return decls;

            Map<String, Collection<T>> mapDecls = new HashMap<>();
            for (T decl : decls) {
                String namespace = decl.getNamespaceName();

                Collection<T> nameList = mapDecls.get(namespace);
                if (nameList == null) {
                    nameList = new ArrayList<>();
                    mapDecls.put(namespace, nameList);
                }
                nameList.add(decl);
            }

            List<String> fullPrioritiesList = new ArrayList<>();
            fullPrioritiesList.add(moduleDeclaration.getNamespace());
            for (LSFNamespaceReference priority : moduleDeclaration.getPriorityRefs())
                fullPrioritiesList.add(priority.getNameRef());
                
            // смотрим на priority
            //noinspection RedundantTypeArguments - отказывается компилироваться с language level 8
            for (String priority : fullPrioritiesList) {
                //noinspection RedundantTypeArguments - отказывается компилироваться с language level 8
                Collection<T> priorityDecls = mapDecls.get(priority);
                if (priorityDecls != null && !priorityDecls.isEmpty()) {
                    decls = priorityDecls;
                    break;
                }
            }
            return finalizer.finalize(decls);
        }
    }

    // classes now are parsed first
    public static boolean isAfter(LSFFile file, int offset, LSFFullNameDeclaration decl) {
        return !(decl instanceof LSFClassDeclaration) && file == decl.getLSFFile() && decl.getOffset() > offset;
    }
    // used only for LSFExtend, should be also stubbed later
    public static boolean isAfter(int offset, LSFDeclaration decl) {
        return decl.getTextOffset() > offset; // later stubs should be added here together with all get*Decls
    }

    public static Collection<LSFModuleDeclaration> findModules(String moduleName, Project project, GlobalSearchScope scope) {
        return LSFGlobalResolver.getItemsFromIndex(ModuleIndex.getInstance(), moduleName, project, scope, LSFLocalSearchScope.GLOBAL);
    }

    public static Query<LSFModuleDeclaration> findModules(String name, GlobalSearchScope scope) {
        return new CollectionQuery<>(findModules(name, scope.getProject(), scope));
    }

    public static Query<LSFNamespaceDeclaration> findNamespaces(String name, GlobalSearchScope scope) {

        Query<LSFNamespaceDeclaration> modules = BaseUtils.immutableCast(findModules(name, scope));
        if (modules.findFirst() != null)
            return modules;

        Collection<LSFExplicitNamespaceDeclaration> explicitDeclarations = LSFGlobalResolver.getItemsFromIndex(ExplicitNamespaceIndex.getInstance(), name, scope.getProject(), scope, LSFLocalSearchScope.GLOBAL);
        if (explicitDeclarations.size() == 0)
            return modules;

        LSFExplicitNamespaceDeclaration minDeclaration = null;
        String minName = null;
        for (LSFExplicitNamespaceDeclaration explicitDecl : explicitDeclarations) {
            String moduleName = explicitDecl.getLSFFile().getModuleDeclaration().getDeclName();
            if (minName == null || minName.compareTo(moduleName) > 0) {
                minDeclaration = explicitDecl;
                minName = moduleName;
            }
        }
        return new MergeQuery<>(modules, new CollectionQuery<>(Collections.<LSFNamespaceDeclaration>singleton(minDeclaration)));
    }

    public static <E extends ExtendStubElement<T, E>, T extends LSFExtend<T, E>> Query<T> findExtendElements(final LSFFullNameDeclaration decl, ExtendStubElementType<T, E> type, LSFFile file, LSFLocalSearchScope localScope) {
        return findExtendElements(decl, type, file.getProject(), getRequireScope(file), localScope);
    }

    public static <E extends ExtendStubElement<T, E>, T extends LSFExtend<T, E>> Query<T> findExtendElements(final LSFFullNameDeclaration decl, ExtendStubElementType<T, E> type, Project project, GlobalSearchScope scope, LSFLocalSearchScope localScope) {
        if (decl == null)
            return new EmptyQuery<>();

        String name = decl.getGlobalName();

//        int elementOffset = element.getTextOffset();
        Collection<T> decls = getItemsFromIndex(type.getGlobalIndex(), name, project, scope, localScope);

        return new FilteredQuery<>(new CollectionQuery<>(decls), t -> {
            LSFFullNameDeclaration resolveDecl = t.resolveDecl();
            return resolveDecl != null && resolveDecl.equals(decl); // проверяем что resolve'ся куда надо
        });
    }

    public static Query<LSFClassExtend> findParentExtends(LSFClassDeclaration decl) {
        Project project = decl.getProject();
        return findExtendElements(decl, LSFStubElementTypes.EXTENDCLASS, project, GlobalSearchScope.allScope(project), LSFLocalSearchScope.createFrom(decl));
    }

    public static Collection<LSFClassDeclaration> findChildrenExtends(LSFClassDeclaration decl, Project project, GlobalSearchScope scope) {

        String name = decl.getGlobalName();

        Collection<LSFClassExtend> classExtends = getItemsFromIndex(ClassExtendsClassIndex.getInstance(), name, project, scope, LSFLocalSearchScope.GLOBAL);

        Collection<LSFClassDeclaration> result = new ArrayList<>();
        for (LSFClassExtend classExtend : classExtends) {
            if (classExtend.resolveExtends().contains(decl)) {
                LSFClassDeclaration thisDecl = (LSFClassDeclaration) classExtend.resolveDecl();
                if (thisDecl != null)
                    result.add(thisDecl);
            }
        }
        return result;
    }
}
