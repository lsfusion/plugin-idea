package com.lsfusion.design.ui;

public interface CheckedListener {
    void nodeChecked(ComponentTreeNode node, boolean checked);

    void onNodeStateChanged(ComponentTreeNode node);
}
