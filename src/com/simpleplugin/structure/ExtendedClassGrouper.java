package com.simpleplugin.structure;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.smartTree.*;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.psi.LSFClassDecl;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;

public class ExtendedClassGrouper implements Grouper {
    @NotNull
    @Override
    public Collection<Group> group(AbstractTreeNode parent, Collection<TreeElement> children) {
        if (isParentGrouped(parent)) {
            return Collections.emptyList();
        }

        LinkedHashMap<LSFClassDecl, Group> groupMap = new LinkedHashMap<LSFClassDecl, Group>();
        for (TreeElement child : children) {
            if (child instanceof LSFPropertyStatementTreeElement) {
                LSFPropertyStatementTreeElement psChild = (LSFPropertyStatementTreeElement) child;
                LSFClassDecl psClass = psChild.getTargetClass();
                ExtendedClassesGroup classGroup = (ExtendedClassesGroup) groupMap.get(psClass);
                if (classGroup == null) {
                    classGroup = new ExtendedClassesGroup(psClass.getName());
                    groupMap.put(psClass, classGroup);
                }
                classGroup.addChild(psChild);
            }
        }

        return groupMap.values();
    }

    private static boolean isParentGrouped(AbstractTreeNode parent) {
        while (parent != null) {
            if (parent.getValue() instanceof ExtendedClassesGroup) {
                return true;
            }
            parent = parent.getParent();
        }
        return false;
    }

    @NotNull
    @Override
    public ActionPresentation getPresentation() {
        return new ActionPresentationData("Group by Class", null, LSFIcons.GROUP_BY_CLASS);
    }

    @NotNull
    @Override
    public String getName() {
        return "CLASSES_GROUPER";
    }
}
