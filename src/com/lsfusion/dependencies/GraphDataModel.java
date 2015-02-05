package com.lsfusion.dependencies;

import com.intellij.openapi.util.Pair;
import org.jgrapht.Graph;
import org.jgrapht.alg.DijkstraShortestPath;

import java.util.*;

public class GraphDataModel {
    private Map<String, GraphNode> nodes = new LinkedHashMap<String, GraphNode>();
    private List<Pair<GraphNode, GraphNode>> edges = new ArrayList<Pair<GraphNode, GraphNode>>();

    public GraphNode rootNode;

    public Collection<GraphNode> getNodes() {
        return nodes.values();
    }

    public GraphNode getNode(String moduleName) {
        return nodes.get(moduleName);
    }

    public boolean containsNode(String name) {
        return nodes.containsKey(name);
    }

    public List<Pair<GraphNode, GraphNode>> getEdges() {
        return edges;
    }
    
    public boolean createEdge(GraphNode sourceNode, GraphNode targetNode, boolean required) {
        nodes.put(sourceNode.getSID(), sourceNode);
        nodes.put(targetNode.getSID(), targetNode);

        if (rootNode == null) {
            rootNode = required ? sourceNode : targetNode;
        }

        Pair<GraphNode, GraphNode> edge = Pair.create(sourceNode, targetNode);
        if (edges.contains(edge)) {
            return false;
        }

        edges.add(edge);

        return true;
    }

    public List getPath(Graph g, String targetElement) {
        List result = new ArrayList();

        GraphNode targetNode = nodes.get(targetElement);
        if (targetNode != null) {
            DijkstraShortestPath p = new DijkstraShortestPath(g, rootNode, targetNode);
            result = p.getPathEdgeList();
        }

        return result;
    }
}
