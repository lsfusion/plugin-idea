package com.lsfusion.design.ui;

import com.intellij.ui.CheckboxTree;

import javax.swing.*;

public class ComponentTreeCellRenderer extends CheckboxTree.CheckboxTreeCellRenderer {

    public ComponentTreeCellRenderer() {
        super(true, false);
    }

    @Override
    public void customizeRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        super.customizeRenderer(tree, value, selected, expanded, leaf, row, hasFocus);
    }

    @Override
    public void customizeCellRenderer(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
        ComponentTreeNode node = (ComponentTreeNode) value;
        node.getComponent().decorateTreeRenderer(getTextRenderer());
    }
}
