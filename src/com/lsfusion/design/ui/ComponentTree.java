package com.lsfusion.design.ui;

import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckedTreeNode;

public class ComponentTree extends CheckboxTree {
    private CheckedListener listener;

    public ComponentTree(ComponentTreeCellRenderer renderer, ComponentTreeNode rootNode, CheckPolicy policy) {
        super(renderer, rootNode, policy);
    }

    @Override
    public void setNodeState(CheckedTreeNode node, boolean checked) {
        super.setNodeState(node, checked);
        if (listener != null) {
            listener.nodeChecked((ComponentTreeNode) node, checked);
        }
    }

    @Override
    protected void onNodeStateChanged(CheckedTreeNode node) {
        listener.onNodeStateChanged((ComponentTreeNode) node);
    }

    public void setCheckedListener(CheckedListener listener) {
        this.listener = listener;
    }
}
