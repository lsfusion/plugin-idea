package com.lsfusion.structure;

import com.intellij.ide.util.treeView.smartTree.Group;
import com.intellij.ide.util.treeView.smartTree.TreeElement;
import com.intellij.navigation.ItemPresentation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class PropertyStatementGroup implements Group, ItemPresentation {
    private List<TreeElement> children;

    public PropertyStatementGroup() {
        children = new ArrayList<>();
    }

    public void addChild(TreeElement child) {
        children.add(child);
    }

    @Override
    public ItemPresentation getPresentation() {
        return this;
    }

    @Override
    public Collection<TreeElement> getChildren() {
        return children;
    }

    @Nullable
    @Override
    public String getLocationString() {
        return null;
    }
}
