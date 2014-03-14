package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBTabbedPane;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.ColumnsPanel;
import com.lsfusion.design.ui.FlexPanel;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ContainerView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("childrenAlignment"),
            new ReflectionProperty("columns"),
            new ReflectionProperty("type"),
            new ReflectionProperty("caption"),
            new ReflectionProperty("description")
    );

    private final List<ComponentView> children = new ArrayList<ComponentView>();

    public String caption;
    public String description;
    public ContainerType type = ContainerType.CONTAINERV;
    public Alignment childrenAlignment = Alignment.LEADING;
    public int columns = 4;

    public ContainerView() {
        this("");
    }

    public ContainerView(String sID) {
        super(sID);
    }

    public List<ComponentView> getChildren() {
        return children;
    }

    public void add(ComponentView child) {
        child.removeFromParent();
        child.setParent(this);
        children.add(child);
    }

    public boolean remove(ComponentView child) {
        return children.remove(child);
    }

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setType(ContainerType type) {
        this.type = type;
    }

    public void setChildrenAlignment(Alignment childrenAlignment) {
        this.childrenAlignment = childrenAlignment;
    }

    public void setColumns(int columns) {
        this.columns = columns;
    }

    public Alignment getChildrenAlignment() {
        return childrenAlignment;
    }

    public int getColumns() {
        return columns;
    }

    public ContainerType getType() {
        return type;
    }

    @Override
    public String getCaption() {
        return caption;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.CONTAINER;
    }

    @Override
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection) {

        JComponent widget;
        if (type.isLinear()) {
            widget = createLinearPanel(project, selection);
        } else if (type.isTabbed()) {
            widget = createTabbedPanel(project, selection);
        } else if (type.isSplit()) {
            widget = createSplitPanel(project, selection);
        } else if (type.isColumns()) {
            widget = createColumnsPanel(project, selection);
        } else {
            throw new IllegalStateException("shouldn't be");
        }
        
        if (widget != null && caption != null && parent != null && !type.isTabbed() && !parent.type.isTabbed()) {
            widget.setBorder(BorderFactory.createTitledBorder(caption));
        }
        
        return widget == null ? null : widget;
    }

    private FlexPanel createLinearPanel(Project project, Map<ComponentView, Boolean> selection) {
        FlexPanel flexPanel = new FlexPanel(type.isVertical(), childrenAlignment);
        boolean hasChildren = false;
        for (ComponentView child : children) {
            JComponent childWidget = child.createWidget(project, selection);
            if (childWidget != null) {
                hasChildren = true;
                flexPanel.add(childWidget, child.flex, child.alignment);
            }
        }
        return hasChildren ? flexPanel : null;
    }

    private JBSplitter createSplitPanel(Project project, Map<ComponentView, Boolean> selection) {
        JBSplitter splitPane = new JBSplitter(type.isVerticalSplit());
        boolean hasChildren = false;
        if (children.size() > 0) {
            JComponent childWidget = children.get(0).createWidget(project, selection);
            if (childWidget != null) {
                hasChildren = true;
                splitPane.setFirstComponent(childWidget);
            }
        }
        if (children.size() > 1) {
            JComponent childWidget = children.get(1).createWidget(project, selection);
            if (childWidget != null) {
                hasChildren = true;
                splitPane.setSecondComponent(childWidget);
            }
        }
        return hasChildren ? splitPane : null;
    }

    private JTabbedPane createTabbedPanel(Project project, Map<ComponentView, Boolean> selection) {
        JTabbedPane tabbedPane = new JBTabbedPane();
        boolean hasChildren = false;
        for (ComponentView child : children) {
            Component childWidget = child.createWidget(project, selection);
            if (childWidget != null) {
                hasChildren = true;
                String caption = child.getCaption();
                if (caption == null) {
                    caption = "";
                }
                tabbedPane.addTab(caption, childWidget);
            }
        }
        return hasChildren ? tabbedPane : null;
    }

    private JComponent createColumnsPanel(Project project, Map<ComponentView, Boolean> selection) {
        List<Component> childrenWidgets = new ArrayList<Component>();
        boolean hasChildren = false;
        for (ComponentView child : children) {
            Component childWidget = child.createWidget(project, selection);
            childrenWidgets.add(childWidget);
            if (childWidget != null) {
                hasChildren = true;
            }
        }

        return hasChildren ? new ColumnsPanel(columns, childrenWidgets) : null;
    }
}
