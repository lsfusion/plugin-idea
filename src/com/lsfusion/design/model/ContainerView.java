package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBSplitter;
import com.intellij.ui.components.JBScrollPane;
import com.intellij.ui.components.JBTabbedPane;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.*;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.lsfusion.design.model.ContainerType.*;
import static java.lang.Math.max;

public class ContainerView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("childrenAlignment"),
            new ReflectionProperty("columns"),
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
    public Alignment childrenAlignment = Alignment.START;
    public int columns = 4;
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

    public void setShowIf(String showIf) {
        this.showIf = showIf;
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
    
    public String getShowIf() {
        return showIf;
    }

    public void add(ComponentView comp) {
        changeContainer(comp);
        children.add(comp);
    }

    private void changeContainer(ComponentView comp) {
        comp.removeFromParent();
        comp.setContainer(this);
    }

    public boolean remove(ComponentView comp) {
        if (children.remove(comp)) {
            comp.setContainer(null);
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

    public static void setSizes(JComponentPanel view, ComponentView child) {
        view.setComponentSize(child.getSize());
    }

    @Override
    protected JComponentPanel createWidgetImpl(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget) {

        JComponentPanel widget = null;

        if (isTabbedPane() || isSplit()) {
            if (oldWidget != null) {
                ContainerType oldType = (ContainerType) oldWidget.getClientProperty("containerType");
                if (oldType != type) {
                    oldWidget = null;
                }
            }

            if (isTabbedPane()) {
                widget = createTabbedPanel(project, formEntity, selection, componentToWidget, oldWidget);
            } else if (isSplit()) {
                widget = createSplitPanel(project, formEntity, selection, componentToWidget, oldWidget);
            }
        } else {
            if (isLinear()) {
                widget = createLinearPanel(project, formEntity, selection, componentToWidget);
            } else if (isScroll()) {
                widget = createScrollPanel(project, formEntity, selection, componentToWidget);
            } else if (isColumns()) {
                widget = createColumnsPanel(project, formEntity, selection, componentToWidget);
            }
        }

        if (widget == null) {
            return null;
        }

        if (caption != null && container != null && !isTabbedPane() && !container.isTabbedPane()) {
            widget.setBorder(BorderFactory.createTitledBorder(caption));
        }

        widget.putClientProperty("containerType", type);

        return widget;
    }

    private JComponentPanel createLinearPanel(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget) {
        FlexPanel flexPanel = new FlexPanel(isLinearVertical(), childrenAlignment);
        boolean hasChildren = false;
        for (ComponentView child : children) {
            JComponentPanel childWidget = child.createWidget(project, formEntity, selection, componentToWidget);
            if (childWidget != null) {
                hasChildren = true;
                flexPanel.add(childWidget, child.getFlex(formEntity), child.getAlignment());
                setSizes(childWidget, child);
            }
        }
        return hasChildren ? flexPanel : null;
    }

    private JComponentPanel createScrollPanel(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget) {
        JBScrollPane scrollPane = new JBScrollPane();
        boolean hasChildren = false;
        JComponentPanel componentPanel = null;
        
        for (ComponentView child : children) {
            JComponent childWidget = child.createWidget(project, formEntity, selection, componentToWidget);
            if (childWidget != null) {
                hasChildren = true;
                scrollPane.setViewportView(childWidget);
            }
            componentPanel = new JComponentPanel(scrollPane);
            setSizes(componentPanel, child);
        }
        JComponentPanel scrollPanel = null;
        if (hasChildren) {
            scrollPanel = new JComponentPanel(componentPanel);
        }

        return scrollPanel;
    }

    private JComponentPanel createSplitPanel(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget) {
        JBSplitter splitPane = new JBSplitter(isSplitVertical());
        boolean hasChildren = false;
        if (children.size() > 0) {
            JComponentPanel childWidget = children.get(0).createWidget(project, formEntity, selection, componentToWidget);
            if (childWidget != null) {
                hasChildren = true;
                splitPane.setFirstComponent(childWidget);
                setSizes(childWidget, children.get(0));
            }
        }
        if (children.size() > 1) {
            JComponentPanel childWidget = children.get(1).createWidget(project, formEntity, selection, componentToWidget);
            if (childWidget != null) {
                hasChildren = true;
                splitPane.setSecondComponent(childWidget);
                setSizes(childWidget, children.get(1));

                double flex1 = children.get(0).getFlex(formEntity);
                double flex2 = children.get(1).getFlex(formEntity);
                if (flex1 == 0 && flex2 == 0) {
                    flex1 = 1;
                    flex2 = 1;
                }
                splitPane.setProportion((float) (flex1 / (flex1 + flex2)));
            }
        }
        return hasChildren ? new JComponentPanel(splitPane) : null;
    }

    private JComponentPanel createTabbedPanel(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget) {
        ComponentView selectedChild = null;
        if (oldWidget != null) {
            TabbedPane oldTabbedPane = (TabbedPane) oldWidget.getComponent(0);
            selectedChild = (ComponentView) ((JComponent) oldTabbedPane.getSelectedComponent()).getClientProperty("componentView");
        }

        JComponent selectedWidget = null;
        TabbedPane tabbedPane = new TabbedPane();
        boolean hasChildren = false;
        for (ComponentView child : getChildren()) {
            JComponentPanel childWidget = child.createWidget(project, formEntity, selection, componentToWidget);
            if (childWidget != null) {
                FlexPanel flexPanel = new FlexPanel(true, Alignment.START);
                flexPanel.add(childWidget, child.getFlex(formEntity), child.getAlignment());
                flexPanel.putClientProperty("componentView", child);
                
                hasChildren = true;
                String caption = child.getCaption();
                if (caption == null) {
                    caption = "";
                }

                tabbedPane.addTab(caption, flexPanel);
                if (child == selectedChild) {
                    selectedWidget = flexPanel;
                }
            }
        }

        if (hasChildren) {
            if (selectedWidget != null) {
                tabbedPane.setSelectedComponent(selectedWidget);
            }
            return new JComponentPanel(tabbedPane);
        }
        return null;
    }

    private JComponentPanel createColumnsPanel(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget) {
        List<Component> childrenWidgets = new ArrayList<>();
        boolean hasChildren = false;
        for (ComponentView child : children) {
            Component childWidget = child.createWidget(project, formEntity, selection, componentToWidget);
            childrenWidgets.add(childWidget);
            if (childWidget != null) {
                hasChildren = true;
            }
        }

        return hasChildren ? new ColumnsPanel(this, formEntity, childrenWidgets) : null;
    }

    public boolean isTabbedPane() {
        return type == TABBED;
    }

    public boolean isColumns() {
        return type == COLUMNS;
    }

    public boolean isScroll() {
        return type == SCROLL;
    }

    public boolean isSplitVertical() {
        return type == SPLITV;
    }

    public boolean isSplitHorizontal() {
        return type == SPLITH;
    }
    
    public boolean isSplit() {
        return isSplitHorizontal() || isSplitVertical();
    }

    public boolean isLinearVertical() {
        return type == CONTAINERV;
    }

    public boolean isLinearHorizontal() {
        return type == CONTAINERH;
    }

    public boolean isLinear() {
        return isLinearVertical() || isLinearHorizontal();
    }

    public boolean isVertical() {
        return isLinearVertical() || isSplitVertical() || isColumns() || isScroll() || isTabbedPane();
    }

    public boolean isHorizontal() {
        return isLinearHorizontal() || isSplitHorizontal();
    }

    private static class TabbedPane extends JBTabbedPane {
        public TabbedPane() {
            super(SwingConstants.TOP, JTabbedPane.SCROLL_TAB_LAYOUT);
        }

        @Override
        public Dimension getMinimumSize() {
            return layoutSize(CachableLayout.minSizeGetter);
        }

        @Override
        public Dimension getPreferredSize() {
            return layoutSize(CachableLayout.prefSizeGetter);
        }

        private Dimension layoutSize(CachableLayout.ComponentSizeGetter sizeGetter) {
            Dimension pref = super.getPreferredSize();

            //заново считаем максимальный размер и вычитаем его, т. к. размеры таб-панели зависят от LAF
            int maxWidth = 0;
            int maxHeight = 0;
            for (int i = 0; i < getTabCount(); i++) {
                Component child = getComponentAt(i);
                if (child != null) {
                    Dimension size = sizeGetter.get(child);
                    if (size != null) {
                        maxWidth = max(maxWidth, size.width);
                        maxHeight = max(maxHeight, size.height);
                    }
                }
            }
            pref.width -= maxWidth;
            pref.height -= maxHeight;

            Component selected = getSelectedComponent();
            if (selected != null) {
                Dimension d = sizeGetter.get(selected);
                pref.width += d.width;
                pref.height += d.height;
            }

            return pref;
        }
    }
}
