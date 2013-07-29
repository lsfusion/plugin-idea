package com.simpleplugin.psi;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.util.*;
import com.simpleplugin.BaseUtils;
import com.simpleplugin.psi.declarations.*;
import com.simpleplugin.psi.references.LSFModuleReference;
import com.simpleplugin.psi.references.LSFNamespaceReference;
import com.simpleplugin.psi.stubs.FullNameStubElement;
import com.simpleplugin.psi.stubs.types.FullNameStubElementType;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;

import java.util.*;

public class LSFGlobalResolver {

    // вот эту хрень надо по хорошему кэшировать
    private static Set<VirtualFile> getRequireModules(LSFModuleDeclaration declaration) {
        Set<VirtualFile> result = new HashSet<VirtualFile>();
        result.add(declaration.getContainingFile().getVirtualFile());
        for(LSFModuleReference ref : declaration.getRequireRefs()) {
            LSFModuleDeclaration resolve = ref.resolveDecl();
            if(resolve!=null)
                result.addAll(getRequireModules(resolve));
        }
        return result;
    }

    private static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Query<T> findInNamespace(Collection<T> decls, Condition<T> condition) {
        for(T decl : decls)
            if(condition.value(decl))
                return new CollectionQuery<T>(Collections.singleton(decl));
        return null;
    }

    public static <S extends FullNameStubElement<S, T>, T extends LSFFullNameDeclaration<T, S>> Query<T> findElements(String name, String fqName, LSFFile file, FullNameStubElementType<S, T> type, Condition<T> condition) {
        StringStubIndexExtension<T> index = type.getGlobalIndex();

        LSFModuleDeclaration moduleDeclaration = file.getModuleDeclaration();
        Project project = file.getProject();

        Collection<T> decls = index.get(name, project, GlobalSearchScope.filesScope(project, getRequireModules(moduleDeclaration)));

        Map<LSFNamespaceDeclaration, Collection<T>> mapDecls = new HashMap<LSFNamespaceDeclaration, Collection<T>>();
        for(T decl : decls) {
            LSFNamespaceDeclaration namespace = ((LSFFile) decl.getContainingFile()).getModuleDeclaration().getNamespace();
            if(fqName!=null && namespace.getGlobalName().equals(fqName) && condition.value(decl))
                return new CollectionQuery<T>(Collections.singleton(decl));

            Collection<T> nameList = mapDecls.get(namespace);
            if(nameList==null) {
                nameList = new ArrayList<T>();
                mapDecls.put(namespace, nameList);
            }
            nameList.add(decl);
        }

        if(fqName!=null) // должны были найти на прошлом шаге
            return new EmptyQuery<T>();

        if(decls.size()>1) { // смотрим на priority
            Query<T> result = findInNamespace(mapDecls.get(moduleDeclaration.getNamespace()), condition);
            if(result!=null)
                return result;

            for(LSFNamespaceReference priority : moduleDeclaration.getPriorityRefs()) {
                LSFNamespaceDeclaration resolve = priority.resolveDecl();
                if(resolve!=null) {
                    result = findInNamespace(mapDecls.get(resolve), condition);
                    if(result!=null)
                        return result;
                }
            }
        }

        return new CollectionQuery<T>(decls);
    }

    public static Query<LSFModuleDeclaration> findModules(String name, Project project) {
        StringStubIndexExtension<LSFModuleDeclaration> index = LSFStubElementTypes.MODULE.getGlobalIndex();
        Collection<LSFModuleDeclaration> declarations = index.get(name, project, GlobalSearchScope.allScope(project));
        return new CollectionQuery<LSFModuleDeclaration>(declarations);
    }

    public static Query<LSFNamespaceDeclaration> findNamespaces(String name, Project project) {

        Query<LSFNamespaceDeclaration> modules = BaseUtils.<LSFNamespaceDeclaration, LSFModuleDeclaration>immutableCast(findModules(name, project));

        // модуля нет, ищем namespace'ы
        StringStubIndexExtension<LSFExplicitNamespaceDeclaration> explicitIndex = LSFStubElementTypes.EXPLICIT_NAMESPACE.getGlobalIndex();
        Collection<LSFExplicitNamespaceDeclaration> explicitDeclarations = explicitIndex.get(name, project, GlobalSearchScope.allScope(project));
        if(explicitDeclarations.size() == 0)
            return modules;

        LSFExplicitNamespaceDeclaration minDeclaration = null;
        String minName = null;
        for(LSFExplicitNamespaceDeclaration explicitDecl : explicitDeclarations) {
            String moduleName = ((LSFFile) explicitDecl.getContainingFile()).getModuleDeclaration().getGlobalName();
            if(minName == null || minName.compareTo(moduleName) > 0) {
                minDeclaration = explicitDecl;
                minName = moduleName;
            }
        }
        return new MergeQuery<LSFNamespaceDeclaration>(modules, new CollectionQuery<LSFNamespaceDeclaration>(Collections.<LSFNamespaceDeclaration>singleton(minDeclaration)));
    }
}
