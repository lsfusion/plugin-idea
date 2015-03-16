package com.lsfusion.dependencies.module;

import com.intellij.ui.SpeedSearchBase;
import com.lsfusion.dependencies.GraphNode;
import org.jetbrains.annotations.Nullable;
import org.jgraph.JGraph;

public abstract class DependencySpeedSearch extends SpeedSearchBase<JGraph> {
    public DependencySpeedSearch(JGraph component) {
        super(component);
        setClearSearchOnNavigateNoMatch(true);
    }

    @Override
    protected int getSelectedIndex() {
        return 0;
    }

    @Nullable
    @Override
    protected String getElementText(Object element) {
        return ((GraphNode) element).getSID();
    }
}
