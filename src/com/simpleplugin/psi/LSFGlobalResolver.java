package com.simpleplugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.util.*;
import com.intellij.util.containers.ConcurrentHashMap;
import com.simpleplugin.BaseUtils;
import com.simpleplugin.psi.declarations.*;
import com.simpleplugin.psi.references.LSFModuleReference;
import com.simpleplugin.psi.references.LSFNamespaceReference;
import com.simpleplugin.psi.stubs.FullNameStubElement;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;

import java.util.*;

public class LSFGlobalResolver {

    private static ConcurrentHashMap<LSFModuleDeclaration, Set<VirtualFile>> cached = new ConcurrentHashMap<LSFModuleDeclaration, Set<VirtualFile>>();
    // вот эту хрень надо по хорошему кэшировать
    private static Set<VirtualFile> getRequireModules(LSFModuleDeclaration declaration) {
        Set<VirtualFile> cachedFiles = cached.get(declaration);
        if(cachedFiles!=null)
            return cachedFiles;

        Set<VirtualFile> result = new HashSet<VirtualFile>();

        result.add(declaration.getContainingFile().getVirtualFile());
        for(LSFModuleReference ref : declaration.getRequireRefs()) {
            LSFModuleDeclaration resolve = ref.resolveDecl();
            if(resolve!=null)
                result.addAll(getRequireModules(resolve));
        }
//        System.out.println("CACHED "+declaration.getName()+" "+System.identityHashCode(declaration));
        cached.put(declaration, result);
        return result;
    }

    private static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Query<T> findInNamespace(Collection<T> decls) {
        if(decls==null)
            return null;

        if(decls.size()>0)
            return new CollectionQuery<T>(decls);
        return null;
    }

    public static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Query<T> findElements(String name, String fqName, LSFFile file, FullNameStubElementType<S, T> type, Condition<T> condition) {
        return findElements(name, fqName, file, type, null, condition);
    }

    public static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Query<T> findElements(String name, String fqName, LSFFile file, FullNameStubElementType<S, T> type, T virtDecl, Condition<T> condition) {
        StringStubIndexExtension<T> index = type.getGlobalIndex();

        LSFModuleDeclaration moduleDeclaration = file.getModuleDeclaration();
        Project project = file.getProject();

        Collection<T> decls = index.get(name, project, GlobalSearchScope.filesScope(project, getRequireModules(moduleDeclaration)));
        if(virtDecl!=null)
            decls.add(virtDecl);

        Map<String, Collection<T>> mapDecls = new HashMap<String, Collection<T>>();
        Collection<T> fitDecls = new ArrayList<T>();
        for(T decl : decls) 
            if(condition.value(decl)) {
                String namespace = decl.getNamespaceName();
                if(fqName!=null && namespace.equals(fqName))
                    return new CollectionQuery<T>(Collections.singleton(decl));
    
                Collection<T> nameList = mapDecls.get(namespace);
                if(nameList==null) {
                    nameList = new ArrayList<T>();
                    mapDecls.put(namespace, nameList);
                }
                nameList.add(decl);
                
                fitDecls.add(decl);
            }

        if(fqName!=null) // должны были найти на прошлом шаге
            return new EmptyQuery<T>();

        if(fitDecls.size()>1) { // смотрим на condition и priority
            Query<T> result = findInNamespace(mapDecls.get(moduleDeclaration.getNamespace()));
            if(result!=null)
                return result;

            for(LSFNamespaceReference priority : moduleDeclaration.getPriorityRefs()) {
                String resolve = priority.getNameRef();
//                if(resolve!=null) {
                    result = findInNamespace(mapDecls.get(resolve));
                    if(result!=null)
                        return result;
//                }
            }
        }

        return new CollectionQuery<T>(fitDecls);
    }

    public static Query<LSFModuleDeclaration> findModules(String name, GlobalSearchScope scope) {
        StringStubIndexExtension<LSFModuleDeclaration> index = LSFStubElementTypes.MODULE.getGlobalIndex();
        Collection<LSFModuleDeclaration> declarations = index.get(name, scope.getProject(), scope);
        return new CollectionQuery<LSFModuleDeclaration>(declarations);
    }

    public static Query<LSFNamespaceDeclaration> findNamespaces(String name, GlobalSearchScope scope) {
        
        Query<LSFNamespaceDeclaration> modules = BaseUtils.<LSFNamespaceDeclaration, LSFModuleDeclaration>immutableCast(findModules(name, scope));
        if(modules.findFirst()!=null)
            return modules;

        // модуля нет, ищем namespace'ы
        StringStubIndexExtension<LSFExplicitNamespaceDeclaration> explicitIndex = LSFStubElementTypes.EXPLICIT_NAMESPACE.getGlobalIndex();
        Collection<LSFExplicitNamespaceDeclaration> explicitDeclarations = explicitIndex.get(name, scope.getProject(), scope);
        if(explicitDeclarations.size() == 0)
            return modules;

        LSFExplicitNamespaceDeclaration minDeclaration = null;
        String minName = null;
        for(LSFExplicitNamespaceDeclaration explicitDecl : explicitDeclarations) {
            String moduleName = explicitDecl.getLSFFile().getModuleDeclaration().getDeclName();
            if(minName == null || minName.compareTo(moduleName) > 0) {
                minDeclaration = explicitDecl;
                minName = moduleName;
            }
        }
        return new MergeQuery<LSFNamespaceDeclaration>(modules, new CollectionQuery<LSFNamespaceDeclaration>(Collections.<LSFNamespaceDeclaration>singleton(minDeclaration)));
    }
}
