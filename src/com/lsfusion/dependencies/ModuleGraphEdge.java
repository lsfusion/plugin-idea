package com.lsfusion.dependencies;

import org.jgrapht.graph.DefaultEdge;

public class ModuleGraphEdge extends DefaultEdge {
    private final ModuleGraphNode sourceNode;
    private final ModuleGraphNode targetNode;

    public ModuleGraphEdge(ModuleGraphNode sourceNode, ModuleGraphNode targetNode) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
    }

    @Override
    protected ModuleGraphNode getSource() {
        return sourceNode;
    }

    @Override
    protected ModuleGraphNode getTarget() {
        return targetNode;
    }
}