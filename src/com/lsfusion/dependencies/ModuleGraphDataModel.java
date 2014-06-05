package com.lsfusion.dependencies;

import com.intellij.openapi.util.Pair;
import com.intellij.ui.JBColor;
import org.jgraph.graph.DefaultGraphCell;
import org.jgraph.graph.GraphConstants;
import org.jgrapht.Graph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultEdge;

import java.awt.*;
import java.util.*;
import java.util.List;

public class ModuleGraphDataModel {
    private Map<String, ModuleGraphNode> nodes = new LinkedHashMap<String, ModuleGraphNode>();
    private List<Pair<ModuleGraphNode, ModuleGraphNode>> edges = new ArrayList<Pair<ModuleGraphNode, ModuleGraphNode>>();

    public ModuleGraphNode rootNode;

    public boolean containsNode(String name) {
        return nodes.containsKey(name);
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

    public void initGraph(Graph g) {
        for (ModuleGraphNode node : nodes.values()) {
            g.addVertex(node);
        }

        for (Pair<ModuleGraphNode, ModuleGraphNode> e : edges) {
            ModuleGrtaphEdge edge = new ModuleGrtaphEdge(e.first, e.second);
            g.addEdge(e.first, e.second, edge);
        }
    }

    public void designGraph(Graph g, JGraphModelAdapter m_jgAdapter) {
        Map cellAttr = new HashMap();
        for (ModuleGraphNode node : nodes.values()) {
            DefaultGraphCell cell = m_jgAdapter.getVertexCell(node);
            if (cell != null) {
                Map attr = cell.getAttributes();
                if (node != rootNode) {
                    JBColor background;
                    if (node.required) {
                        background = new JBColor(new Color(0, 173, 57), JBColor.GREEN);
                    } else {
                        background = new JBColor(new Color(48, 117, 255), JBColor.BLUE);
                    }
                    GraphConstants.setBackground(attr, background);
                }
                GraphConstants.setAutoSize(attr, true);
                GraphConstants.setInset(attr, 3);

                cellAttr.put(cell, attr);
            }
        }

        for (Object gEdge : g.edgeSet()) {
            DefaultGraphCell cell = m_jgAdapter.getEdgeCell(gEdge);
            if (cell != null) {
                Map attr = cell.getAttributes();
                GraphConstants.setLabelEnabled(attr, false);

                if ((((ModuleGrtaphEdge) gEdge).getSource()).required) {
                    GraphConstants.setLineColor(attr, new JBColor(new Color(0, 173, 57), JBColor.GREEN));
                }

                cellAttr.put(cell, attr);
            }
        }

        m_jgAdapter.edit(cellAttr, null, null, null);
    }

    private class ModuleGrtaphEdge extends DefaultEdge {
        private final ModuleGraphNode sourceNode;
        private final ModuleGraphNode targetNode;

        public ModuleGrtaphEdge(ModuleGraphNode sourceNode, ModuleGraphNode targetNode) {
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
}
