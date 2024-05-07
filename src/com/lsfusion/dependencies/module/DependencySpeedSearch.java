package com.lsfusion.dependencies.module;

import com.intellij.ui.SpeedSearchBase;
import com.lsfusion.dependencies.GraphNode;
import org.jetbrains.annotations.Nullable;
import org.jgraph.JGraph;

public abstract class DependencySpeedSearch extends SpeedSearchBase<JGraph> {
    protected int selectedIndex = 0;
    protected GraphNode[] nodes;
    
    public DependencySpeedSearch(JGraph component) {
        super(component);
        setClearSearchOnNavigateNoMatch(true);
        nodes = getNodes();
    }

    @Override
    protected int getElementCount() {
        return nodes.length;
    }

    @Override
    protected Object getElementAt(int viewIndex) {
        return nodes[viewIndex];
    }

    @Override
    protected int getSelectedIndex() {
        return selectedIndex;
    }

    @Nullable
    @Override
    protected String getElementText(Object element) {
        return ((GraphNode) element).getSID();
    }
    
    protected abstract GraphNode[] getNodes();
}
