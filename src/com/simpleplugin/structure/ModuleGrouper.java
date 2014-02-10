package com.simpleplugin.structure;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.smartTree.*;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFPropertyStatement;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

public class ModuleGrouper implements Grouper {
    @NotNull
    @Override
    public Collection<Group> group(AbstractTreeNode parent, Collection<TreeElement> children) {
        if (isParentGrouped(parent)) {
            return Collections.emptyList();
        }

        LinkedHashMap<String, Group> map = new LinkedHashMap<String, Group>();

        for (TreeElement child : children) {
            if (child instanceof LSFPropertyStatementTreeElement) {
                LSFPropertyStatementTreeElement psChild = (LSFPropertyStatementTreeElement) child;
                LSFPropertyStatement propStatement = psChild.getElement();
                String moduleName = ((LSFFile) propStatement.getContainingFile()).getModuleDeclaration().getName();
                ModuleGroup group = (ModuleGroup) map.get(moduleName);
                if (group == null) {
                    group = new ModuleGroup(moduleName);
                    map.put(moduleName, group);
                }
                group.addChild(psChild);
            }
        }

        return map.values();
    }

    private static boolean isParentGrouped(AbstractTreeNode parent) {
        while (parent != null) {
            if (parent.getValue() instanceof ModuleGroup) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    @NotNull
    @Override
    public ActionPresentation getPresentation() {
        return new ActionPresentationData("Group by Module", null, LSFIcons.GROUP_BY_MODULE);
    }

    @NotNull
    @Override
    public String getName() {
        return "MODULE_GROUPER";
    }
}
