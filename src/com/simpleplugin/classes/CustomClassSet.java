package com.simpleplugin.classes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Query;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.extend.LSFClassExtend;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class CustomClassSet implements LSFClassSet {
    
    private final Set<LSFClassDeclaration> classes;

    public CustomClassSet(LSFClassDeclaration classDecl) {
        this.classes = Collections.singleton(classDecl);
    }

    public CustomClassSet(Set<LSFClassDeclaration> classes) {
        this.classes = classes;
    }

    public CustomClassSet or(CustomClassSet set) {
        Set<LSFClassDeclaration> orSet = new HashSet<LSFClassDeclaration>(classes);
        orSet.addAll(set.classes);
        return new CustomClassSet(orSet);
    }
    
    private Query<LSFClassExtend> resolveExtendElements(LSFClassDeclaration decl) {
        Project project = decl.getProject();
        return LSFGlobalResolver.findExtendElements(decl, LSFStubElementTypes.EXTENDCLASS, project, GlobalSearchScope.allScope(project));
    }
    
    public boolean recContainsAll(LSFClassDeclaration decl) {
        if(classes.contains(decl))
            return true;
        
        for(LSFClassExtend extDecl : resolveExtendElements(decl))
            for(LSFClassDeclaration inhDecl : extDecl.resolveExtends())
                if(recContainsAll(inhDecl))
                    return true;
        return false;
    }
    
    public boolean containsAll(CustomClassSet set) {
        for(LSFClassDeclaration setClass : set.classes)
            if(!recContainsAll(setClass))
                return false;
        return true;
    }

    public boolean equals(Object o) {
        return this == o || o instanceof CustomClassSet && classes.equals(((CustomClassSet) o).classes);
    }

    public int hashCode() {
        return classes.hashCode();
    }
}
