package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.ui.FlexAlignment;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import java.util.Map;

public class TreeGroupView extends ComponentView implements GroupView {
    public ToolbarView toolbar = new ToolbarView();
    public FilterView filter = new FilterView();

    public TreeGroupView() {
        this("");
    }

    public TreeGroupView(String sID) {
        super(sID);
        flex = 1;
        alignment = FlexAlignment.STRETCH;
    }

    @Override
    public String getCaption() {
        return "Tree";
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.TREE_GROUP;
    }

    @Override
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        TreeTable tree = new TreeTable(new DumbTreeTableModel());
        tree.setShowGrid(true);
        return new JBScrollPane(tree);
    }

    private static class DumbTreeTableModel extends DefaultTreeModel implements TreeTableModel {
        public DumbTreeTableModel() {
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

        @Override
        public int getColumnCount() {
            return 5;
        }

        @Override
        public String getColumnName(int column) {
            if (column == 0) {
                return "Tree";
            }
            return "property";
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
    }
}
