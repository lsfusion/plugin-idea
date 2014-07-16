package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.TreeGroupEntity;
import com.lsfusion.design.ui.FlexAlignment;

import javax.swing.*;
import java.util.List;
import java.util.Map;

public class TreeGroupView extends ComponentView implements GroupView {
    public TreeGroupEntity entity;

    public ToolbarView toolbar;
    public FilterView filter = new FilterView();

    private TreeGroupTableModel model = new TreeGroupTableModel();

    public TreeGroupView(TreeGroupEntity entity) {
        super(entity.sID);
        flex = 1;
        alignment = FlexAlignment.STRETCH;

        this.entity = entity;
        toolbar = new ToolbarView(true);
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
        return new JBScrollPane(new TreeGroupTable(this, model));
    }

    public void addPropertyDraw(GroupObjectView groupObject, PropertyDrawView property, List<PropertyDrawView> formProperties) {
        model.addPropertyDraw(groupObject, property, formProperties);
        toolbar.visible = true;
    }

    public int calculatePreferredSize() {
        int size = 0;
        for (GroupObjectEntity groupObject : entity.groups) {
            size += groupObject.isParent ? 35 * 4 : 35;
        }
        return size;
    }
}
