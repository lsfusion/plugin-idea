package com.lsfusion.dependencies;

import com.intellij.openapi.util.Pair;
import org.jgrapht.Graph;
import org.jgrapht.alg.DijkstraShortestPath;

import java.util.*;

public class ModuleGraphDataModel {
    private Map<String, ModuleGraphNode> nodes = new LinkedHashMap<String, ModuleGraphNode>();
    private List<Pair<ModuleGraphNode, ModuleGraphNode>> edges = new ArrayList<Pair<ModuleGraphNode, ModuleGraphNode>>();

    public ModuleGraphNode rootNode;

    public Collection<ModuleGraphNode> getNodes() {
        return nodes.values();
    }

    public ModuleGraphNode getNode(String moduleName) {
        return nodes.get(moduleName);
    }

    public boolean containsNode(String name) {
        return nodes.containsKey(name);
    }

    public List<Pair<ModuleGraphNode, ModuleGraphNode>> getEdges() {
        return edges;
    }
    
    public boolean createEdge(String sourceName, String targetName, boolean required) {
        ModuleGraphNode sourceNode = nodes.get(sourceName);
        if (sourceNode == null) {
            sourceNode = new ModuleGraphNode(sourceName, required);
            nodes.put(sourceNode.name, sourceNode);
        }
        ModuleGraphNode targetNode = nodes.get(targetName);
        if (targetNode == null) {
            targetNode = new ModuleGraphNode(targetName, required);
            nodes.put(targetNode.name, targetNode);
        }

        if (rootNode == null) {
            rootNode = required ? sourceNode : targetNode;
        }

        if (edges.contains(new Pair<ModuleGraphNode, ModuleGraphNode>(sourceNode, targetNode))) {
            return false;
        }

        edges.add(new Pair<ModuleGraphNode, ModuleGraphNode>(sourceNode, targetNode));

        return true;
    }

    public List getPath(Graph g, String targetModule) {
        List result = new ArrayList();

        ModuleGraphNode targetNode = nodes.get(targetModule);
        if (targetNode != null) {
            DijkstraShortestPath p = new DijkstraShortestPath(g, rootNode, targetNode);
            result = p.getPathEdgeList();
        }

        return result;
    }
}
