package com.lsfusion.design.model;

import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.lsfusion.util.BaseUtils;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TreeGroupTableModel extends DefaultTreeModel implements TreeTableModel {
    public final List<PropertyDrawView> properties = new ArrayList<>();
    public final List<PropertyDrawView> columnProperties = new ArrayList<>();
    public final Map<GroupObjectView, List<PropertyDrawView>> groupPropsMap = new HashMap<>();

    public TreeGroupTableModel() {
        super(null);
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("root");

        DefaultMutableTreeNode category1 = new DefaultMutableTreeNode("category1");
        DefaultMutableTreeNode category2 = new DefaultMutableTreeNode("category2");

        root.add(category1);
        root.add(category2);

        category1.add(new DefaultMutableTreeNode("subCategory11"));
        category1.add(new DefaultMutableTreeNode("subCategory12"));

        category2.add(new DefaultMutableTreeNode("subCategory21"));
        category2.add(new DefaultMutableTreeNode("subCategory22"));

        setRoot(root);
    }

    public void addPropertyDraw(GroupObjectView groupObject, PropertyDrawView property, List<PropertyDrawView> formProperties) {
        if (properties.indexOf(property) == -1) {
            int ins = BaseUtils.relativePosition(property, formProperties, properties);
            properties.add(ins, property);

            List<PropertyDrawView> groupProperties = groupPropsMap.get(groupObject);
            if (groupProperties == null) {
                groupProperties = new ArrayList<>();
                groupPropsMap.put(groupObject, groupProperties);
            }
            int gins = BaseUtils.relativePosition(property, properties, groupProperties);
            groupProperties.add(gins, property);

            if (groupObject.isLastGroupInTree()) {
                int tins = BaseUtils.relativePosition(property, properties, columnProperties);
                columnProperties.add(tins, property);
            }
        }
    }

    @Override
    public int getColumnCount() {
        return columnProperties.size() + 1;
    }

    @Override
    public String getColumnName(int column) {
        if (column == 0) {
            return "Дерево";
        }
        return columnProperties.get(column - 1).getCaption();
    }

    @Override
    public Class getColumnClass(int column) {
        if (column == 0) {
            return TreeTableModel.class;
        }
        return String.class;
    }

    @Override
    public Object getValueAt(Object node, int column) {
        if (column == 0) {
            return node;
        }
        return "";
    }

    @Override
    public boolean isCellEditable(Object node, int column) {
        return false;
    }

    @Override
    public void setValueAt(Object aValue, Object node, int column) {
    }

    @Override
    public void setTree(JTree tree) {
    }

    public PropertyDrawView getColumnProperty(int column) {
        if (column <= 0 || column > columnProperties.size()) {
            return null;
        }
        return columnProperties.get(column - 1);
    }
}
