package com.lsfusion.design.ui;

import com.intellij.ui.CheckboxTree;
import com.intellij.ui.CheckedTreeNode;

public class ComponentTree extends CheckboxTree {
    private CheckedListener listener;

    public ComponentTree(ComponentTreeCellRenderer renderer, ComponentTreeNode rootNode, CheckPolicy policy) {
        super(renderer, rootNode, policy);
    }

    @Override
    protected void checkNode(CheckedTreeNode node, boolean checked) {
        super.checkNode(node, checked);
        if (listener != null) {
            listener.nodeChecked((ComponentTreeNode) node, checked);
        }
    }

    @Override
    protected void onNodeStateChanged(CheckedTreeNode node) {
        listener.onNodeStateChanged((ComponentTreeNode) node);
    }

    public CheckedListener getListener() {
        return listener;
    }

    public void setCheckedListener(CheckedListener listener) {
        this.listener = listener;
    }
}
