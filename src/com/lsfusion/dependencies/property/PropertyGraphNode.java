package com.lsfusion.dependencies.property;

import com.intellij.openapi.project.DumbService;
import com.intellij.pom.Navigatable;
import com.lsfusion.dependencies.GraphNode;
import com.lsfusion.lang.psi.cache.PropertyComplexityCache;
import com.lsfusion.lang.psi.declarations.LSFActionOrGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;

public class PropertyGraphNode implements GraphNode {
    private String name;
    private boolean dependency;
    public LSFActionOrGlobalPropDeclaration property;
    private int complexity;
    
    public PropertyGraphNode(LSFActionOrGlobalPropDeclaration property, boolean dependency) {
        this.dependency = dependency;
        this.property = property;
        
        complexity = property instanceof LSFGlobalPropDeclaration 
                ? DumbService.getInstance(property.getProject()).runReadActionInSmartMode(
                        () -> PropertyComplexityCache.getInstance(property.getProject()).resolveWithCaching((LSFGlobalPropDeclaration)property)
                ) 
                : 0;
        name = property.getPresentableText();
    }

    @Override
    public String getSID() {
        return name;
    }

    @Override
    public boolean isDependent() {
        return !dependency;
    }

    @Override
    public void navigate() {
        ((Navigatable) property).navigate(false);
    }

    @Override
    public String toString() {
        return name + " [" + complexity + "]";
    }
}
