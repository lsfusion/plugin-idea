package com.lsfusion.dependencies.property;

import com.intellij.pom.Navigatable;
import com.lsfusion.dependencies.GraphNode;
import com.lsfusion.lang.psi.cache.PropertyComplexityCache;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;

public class PropertyGraphNode implements GraphNode {
    private String name;
    private boolean dependency;
    public LSFPropDeclaration property;
    private int complexity;
    
    public PropertyGraphNode(LSFPropDeclaration property, boolean dependency) {
        this.dependency = dependency;
        this.property = property;
        
        complexity = PropertyComplexityCache.getInstance(property.getProject()).resolveWithCaching(property);
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
