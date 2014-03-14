package com.lsfusion.design.ui;

public interface CheckedListener {
    public void nodeChecked(ComponentTreeNode node, boolean checked);

    void onNodeStateChanged(ComponentTreeNode node);
}
