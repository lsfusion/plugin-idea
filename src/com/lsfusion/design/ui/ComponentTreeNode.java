package com.lsfusion.design.ui;

import com.intellij.ui.CheckedTreeNode;
import com.lsfusion.design.model.ComponentView;

public class ComponentTreeNode extends CheckedTreeNode {
    private final ComponentView component;

    public ComponentTreeNode(final ComponentView component) {
        super(component);
        this.component = component;
    }

    public ComponentView getComponent() {
        return component;
    }
}
