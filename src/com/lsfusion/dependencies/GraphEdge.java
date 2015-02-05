package com.lsfusion.dependencies;

import org.jgrapht.graph.DefaultEdge;

public class GraphEdge extends DefaultEdge {
    private final GraphNode sourceNode;
    private final GraphNode targetNode;

    public GraphEdge(GraphNode sourceNode, GraphNode targetNode) {
        this.sourceNode = sourceNode;
        this.targetNode = targetNode;
    }

    @Override
    protected GraphNode getSource() {
        return sourceNode;
    }

    @Override
    protected GraphNode getTarget() {
        return targetNode;
    }
}