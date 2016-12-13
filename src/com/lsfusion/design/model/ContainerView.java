package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
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
            new ReflectionProperty("columnLabelsWidth"),
            new ReflectionProperty("type"),
            new ReflectionProperty("caption"),
            new ReflectionProperty("description"),
            new ReflectionProperty("showIf").setExpert()
    );

    private final List<ComponentView> children = new ArrayList<>();

    public String caption;
    public String description;
    @NotNull
    public ContainerType type = ContainerType.CONTAINERV;
    public Alignment childrenAlignment = Alignment.LEADING;
    public int columns = 4;
    public int columnLabelsWidth = 0;
    public String showIf;

    public ContainerView() {
        this("");
    }

    public ContainerView(String sID) {
        super(sID);
    }

    public List<ComponentView> getChildren() {
        return children;
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

    public void setColumnLabelsWidth(int columnLabelsWidth) {
        this.columnLabelsWidth = columnLabelsWidth;
    }
    
    public void setShowIf(String showIf) {
        this.showIf = showIf;
    }

    public Alignment getChildrenAlignment() {
        return childrenAlignment;
    }

    public int getColumns() {
        return columns;
    }

    public int getColumnLabelsWidth() {
        return columnLabelsWidth;
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
    
    public String getShowIf() {
        return showIf;
    }

    public void add(ComponentView comp) {
        changeContainer(comp);
        children.add(comp);
    }

    private void changeContainer(ComponentView comp) {
        comp.removeFromParent();
        comp.setParent(this);
    }

    public boolean remove(ComponentView comp) {
        if (children.remove(comp)) {
            comp.setParent(null);
            return true;
        } else {
            return false;
        }
    }

    public void add(int index, ComponentView comp) {
        changeContainer(comp);
        children.add(index, comp);
    }

    public void addBefore(ComponentView comp, ComponentView compBefore) {
        if (!children.contains(compBefore)) {
            add(comp);
        } else {
            remove(comp);
            add(children.indexOf(compBefore), comp);
        }
    }

    public void addAfter(ComponentView comp, ComponentView compAfter) {
        if (!children.contains(compAfter)) {
            add(comp);
        } else {
            remove(comp);
            add(children.indexOf(compAfter) + 1, comp);
        }
    }

    public void addFirst(ComponentView comp) {
        add(0, comp);
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
            } else if (type.isScroll()) {
                widget = createScrollPanel(project, selection, componentToWidget);
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

    private JBScrollPane createScrollPanel(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget) {
        JBScrollPane scrollPane = new JBScrollPane();
        boolean hasChildren = false;
        for (ComponentView child : children) {
            JComponent childWidget = child.createWidget(project, selection, componentToWidget);
            if (childWidget != null) {
                hasChildren = true;
                scrollPane.setPreferredSize(childWidget.getPreferredSize());
                scrollPane.setViewportView(childWidget);
            }
        }
        return hasChildren ? scrollPane : null;
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

                double flex1 = children.get(0).flex;
                double flex2 = children.get(1).flex;
                if (flex1 == 0 && flex2 == 0) {
                    flex1 = 1;
                    flex2 = 1;
                }
                splitPane.setProportion((float) (flex1 / (flex1 + flex2)));
            }
        }
        return hasChildren ? splitPane : null;
    }

    private JTabbedPane createTabbedPanel(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        ComponentView selectedChild = null;
        if (oldWidget != null) {
            JBTabbedPane oldTabbedPane = (JBTabbedPane) oldWidget;
            selectedChild = (ComponentView) ((JComponent) oldTabbedPane.getSelectedComponent()).getClientProperty("componentView");
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
        List<Component> childrenWidgets = new ArrayList<>();
        boolean hasChildren = false;
        for (ComponentView child : children) {
            Component childWidget = child.createWidget(project, selection, componentToWidget);
            childrenWidgets.add(childWidget);
            if (childWidget != null) {
                hasChildren = true;
            }
            if (columnLabelsWidth > 0 && childWidget instanceof HasLabel) {
                ((HasLabel) childWidget).setLabelWidth(columnLabelsWidth);
            }
        }

        return hasChildren ? new ColumnsPanel(columns, childrenWidgets) : null;
    }
}
