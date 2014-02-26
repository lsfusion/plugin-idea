package com.simpleplugin.structure;

import com.intellij.ide.util.treeView.AbstractTreeNode;
import com.intellij.ide.util.treeView.smartTree.*;
import com.simpleplugin.LSFIcons;
import com.simpleplugin.classes.LSFValueClass;
import com.simpleplugin.psi.declarations.LSFClassDeclaration;
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

        LinkedHashMap<LSFValueClass, Group> groupMap = new LinkedHashMap<LSFValueClass, Group>();
        for (TreeElement child : children) {
            if (child instanceof LSFPropertyStatementTreeElement) {
                LSFPropertyStatementTreeElement psChild = (LSFPropertyStatementTreeElement) child;
                LSFValueClass psClass = psChild.getValueClass();
                ExtendedClassesGroup classGroup = (ExtendedClassesGroup) groupMap.get(psClass);
                if (classGroup == null) {
                    String namespace = null;
                    if (psClass instanceof LSFClassDeclaration) {
                        namespace = ((LSFClassDeclaration) psClass).getLSFFile().getModuleDeclaration().getNamespace();
                    }
                    classGroup = new ExtendedClassesGroup(psClass.getName(), namespace);
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
