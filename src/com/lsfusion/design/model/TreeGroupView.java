package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.components.JBScrollPane;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.FormView;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.TreeGroupEntity;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.design.ui.JComponentPanel;

import javax.swing.*;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class TreeGroupView extends ComponentView implements PropertyGroupContainerView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("expandOnClick")
    );
    
    public TreeGroupEntity entity;

    public ToolbarView toolbarSystem;
    public FilterView userFilter = new FilterView();

    private TreeGroupTableModel model = new TreeGroupTableModel();
    public boolean expandOnClick;

    public TreeGroupView(TreeGroupEntity entity) {
        super(entity.sID);

        this.entity = entity;
        toolbarSystem = new ToolbarView(true);
    }

    @Override
    public double getBaseDefaultFlex(FormEntity formEntity) {
        return 1;
    }

    @Override
    public FlexAlignment getBaseDefaultAlignment(ContainerView container) {
        return FlexAlignment.STRETCH;
    }

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
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
    protected JComponentPanel createWidgetImpl(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget, HashSet<ComponentView> recursionGuard) {
        JBScrollPane scrollPane = new JBScrollPane(new TreeGroupTable(this, model)) {
            @Override
            public boolean isValidateRoot() {
                return false;
            }
        };

        return new JComponentPanel(scrollPane);
    }

    public void addPropertyDraw(GroupObjectView groupObject, PropertyDrawView property, List<PropertyDrawView> formProperties) {
        model.addPropertyDraw(groupObject, property, formProperties);
        toolbarSystem.visible = true;
    }

    public int calculateSize() {
        int size = 0;
        for (GroupObjectEntity groupObject : entity.groups) {
            size += groupObject.isParent ? 35 * 4 : 35;
        }
        return size;
    }
    
    public void setExpandOnClick(boolean expandOnClick) {
        this.expandOnClick = expandOnClick;
    }
    
    public boolean getExpandOnClick() {
        return expandOnClick;
    }

    @Override
    public String getPropertyGroupContainerSID() {
        return FormView.getTreeSID(entity.sID);
    }
}
