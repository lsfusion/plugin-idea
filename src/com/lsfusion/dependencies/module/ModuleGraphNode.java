package com.lsfusion.dependencies.module;

import com.intellij.pom.Navigatable;
import com.lsfusion.dependencies.GraphNode;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;

public class ModuleGraphNode implements GraphNode {
    public String name;

    private LSFModuleDeclaration module;
    public boolean required;

    public ModuleGraphNode(LSFModuleDeclaration module, boolean required) {
        this.module = module;
        this.required = required;
        name = module.getDeclName();
    }

    @Override
    public String getSID() {
        return name;
    }

    @Override
    public boolean isDependent() {
        return !required;
    }

    @Override
    public String toString() {
        return name;
    }

    @Override
    public void navigate() {
        ((Navigatable) module).navigate(false);    
    }
}
