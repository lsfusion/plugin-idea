package com.simpleplugin.classes;

import com.intellij.openapi.project.Project;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.util.Query;
import com.simpleplugin.psi.LSFGlobalResolver;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
import com.simpleplugin.psi.extend.LSFClassExtend;
import com.simpleplugin.psi.stubs.types.LSFStubElementTypes;

import java.util.*;

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
    
    public static Collection<LSFClassDeclaration> getChildren(LSFClassDeclaration decl) {
        Project project = decl.getProject();
        return LSFGlobalResolver.findClassExtends(decl, project, GlobalSearchScope.allScope(project));
    }

    public LSFClassDeclaration haveCommonChilds(CustomClassSet set) {
        for(LSFClassDeclaration setClass : set.classes) // оптимизация
            if(containsAll(setClass))
                return setClass;
        for(LSFClassDeclaration setClass : classes) // оптимизация
            if(set.containsAll(setClass))
                return setClass;
        
        Map<LSFClassDeclaration, Boolean> map = new HashMap<LSFClassDeclaration, Boolean>();
        for(LSFClassDeclaration aClass : classes)
            map.put(aClass, true);
        for(LSFClassDeclaration aClass : set.classes)
            map.put(aClass, false);
        
        int i=0;
        List<LSFClassDeclaration> list = new ArrayList<LSFClassDeclaration>();
        list.addAll(classes);
        list.addAll(set.classes);
        while(i < list.size()) {
            LSFClassDeclaration decl = list.get(i);
            boolean side = map.get(decl);

            for(LSFClassDeclaration child : getChildren(decl)) {
                Boolean childSide = map.get(child);
                if(childSide != null && side != childSide)
                    return child;
                if(childSide == null) {
                    list.add(child);
                    map.put(child, side);
                }                
            }

            i++;
        }
        return null;
    }

    public boolean containsAll(LSFClassDeclaration decl) {
        for(LSFClassDeclaration declClass : classes)
            if(declClass.getGlobalName().equals("Object") && declClass.getLSFFile().getModuleDeclaration().getGlobalName().equals("System"))
                return true;                
        
        return recContainsAll(decl, new HashSet<LSFClassDeclaration>());
    }
    
    public boolean recContainsAll(LSFClassDeclaration decl, Set<LSFClassDeclaration> recursionGuard) {
        if(!recursionGuard.add(decl))
            return false;
        
        if(classes.contains(decl))
            return true;
        
        for(LSFClassExtend extDecl : resolveExtendElements(decl))
            for(LSFClassDeclaration inhDecl : extDecl.resolveExtends())
                if(recContainsAll(inhDecl, recursionGuard))
                    return true;
        return false;
    }
    
    public boolean containsAll(CustomClassSet set) {
        for(LSFClassDeclaration setClass : set.classes)
            if(!containsAll(setClass))
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
