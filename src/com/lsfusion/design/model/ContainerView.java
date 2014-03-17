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
import org.jetbrains.annotations.NotNull;

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
    @NotNull
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
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        
        JComponent widget = null;
        
        if (type.isTabbed() || type.isSplit()) {
            if (oldWidget != null) {
                ContainerType oldType = (ContainerType) oldWidget.getClientProperty("containerType");
                if (oldType != type) {
                    oldWidget = null;
                }
            }
            
            if (type.isTabbed()) {
                widget = createTabbedPanel(project, selection, componentToWidget, oldWidget);
            } else if (type.isSplit()) {
                widget = createSplitPanel(project, selection, componentToWidget, oldWidget);
            }
        } else {
            if (type.isLinear()) {
                widget = createLinearPanel(project, selection, componentToWidget);
            } else if (type.isColumns()) {
                widget = createColumnsPanel(project, selection, componentToWidget);
            }
        }

        if (widget == null) {
            return null;
        }

        if (caption != null && parent != null && !type.isTabbed() && !parent.type.isTabbed()) {
            widget.setBorder(BorderFactory.createTitledBorder(caption));
        }
        
        widget.putClientProperty("containerType", type);

        return widget;
    }

    private FlexPanel createLinearPanel(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget) {
        FlexPanel flexPanel = new FlexPanel(type.isVertical(), childrenAlignment);
        boolean hasChildren = false;
        for (ComponentView child : children) {
            JComponent childWidget = child.createWidget(project, selection, componentToWidget);
            if (childWidget != null) {
                hasChildren = true;
                flexPanel.add(childWidget, child.flex, child.alignment);
            }
        }
        return hasChildren ? flexPanel : null;
    }

    private JBSplitter createSplitPanel(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        JBSplitter splitPane = new JBSplitter(type.isVerticalSplit());
        boolean hasChildren = false;
        if (children.size() > 0) {
            JComponent childWidget = children.get(0).createWidget(project, selection, componentToWidget);
            if (childWidget != null) {
                hasChildren = true;
                splitPane.setFirstComponent(childWidget);
            }
        }
        if (children.size() > 1) {
            JComponent childWidget = children.get(1).createWidget(project, selection, componentToWidget);
            if (childWidget != null) {
                hasChildren = true;
                splitPane.setSecondComponent(childWidget);
            }
        }
        return hasChildren ? splitPane : null;
    }

    private JTabbedPane createTabbedPanel(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        ComponentView selectedChild = null;
        if (oldWidget != null) {
            JBTabbedPane oldTabbedPane = (JBTabbedPane) oldWidget;
            selectedChild = (ComponentView) ((JComponent)oldTabbedPane.getSelectedComponent()).getClientProperty("componentView");
        }
        
        JComponent selectedWidget = null;
        JBTabbedPane tabbedPane = new JBTabbedPane();
        boolean hasChildren = false;
        for (ComponentView child : children) {
            JComponent childWidget = child.createWidget(project, selection, componentToWidget);
            if (childWidget != null) {
                hasChildren = true;
                String caption = child.getCaption();
                if (caption == null) {
                    caption = "";
                }
                childWidget.putClientProperty("componentView", child);
                tabbedPane.addTab(caption, childWidget);
                if (child == selectedChild) {
                    selectedWidget = childWidget;
                }
            }
        }
        
        if (hasChildren) {
            if (selectedWidget != null) {
                tabbedPane.setSelectedComponent(selectedWidget);
            }
            return tabbedPane;
        }
        return null;
    }

    private JComponent createColumnsPanel(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget) {
        List<Component> childrenWidgets = new ArrayList<Component>();
        boolean hasChildren = false;
        for (ComponentView child : children) {
            Component childWidget = child.createWidget(project, selection, componentToWidget);
            childrenWidgets.add(childWidget);
            if (childWidget != null) {
                hasChildren = true;
            }
        }

        return hasChildren ? new ColumnsPanel(columns, childrenWidgets) : null;
    }
}
