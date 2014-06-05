package com.lsfusion.dependencies;

public class ModuleGraphNode {
    public String name;

    public boolean required;

    public ModuleGraphNode(String name, boolean required) {
        this.name = name;
        this.required = required;
    }

    @Override
    public String toString() {
        return name;
    }
}
