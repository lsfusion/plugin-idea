package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
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
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.lsfusion.design.model.ContainerType.*;
import static java.lang.Math.max;

public class ContainerView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("childrenAlignment"),
            new ReflectionProperty("columns"), //backward compatibility
            new ReflectionProperty("lines"),
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
    public int lines = 1;
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

    public void setLines(int lines) {
        this.lines = lines;
    }

    public void setShowIf(String showIf) {
        this.showIf = showIf;
    }

    public int getLines() {
        return lines;
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
    protected JComponentPanel createWidgetImpl(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget, HashSet<ComponentView> recursionGuard) {

        JComponentPanel widget = null;

        if (isTabbedPane()) {
            if (oldWidget != null) {
                ContainerType oldType = (ContainerType) oldWidget.getClientProperty("containerType");
                if (oldType != type) {
                    oldWidget = null;
                }
            }
            widget = createTabbedPanel(project, formEntity, selection, componentToWidget, oldWidget, recursionGuard);
        } else {
            if (isLinear() || isScroll() || isColumns() || isSplit()) {
                widget = createLinearPanel(project, formEntity, selection, componentToWidget, recursionGuard);
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

    private JComponentPanel createLinearPanel(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, HashSet<ComponentView> recursionGuard) {
        FlexPanel flexPanel;
        if(isSimple()) {
            flexPanel = new FlexPanel(isVertical(), childrenAlignment);
            for (ComponentView child : children) {
                if (!recursionGuard.contains(child)) {
                    recursionGuard.add(child);
                    JComponentPanel childWidget = child.createWidget(project, formEntity, selection, componentToWidget, recursionGuard);
                    if (childWidget != null) {
                        flexPanel.add(childWidget, child.getFlex(formEntity), child.getAlignment());
                        setSizes(childWidget, child);
                    }
                }
            }
        } else {
            List<Component> childrenWidgets = new ArrayList<>();
            boolean hasChildren = false;
            for (ComponentView child : children) {
                if (!recursionGuard.contains(child)) {
                    recursionGuard.add(child);
                    Component childWidget = child.createWidget(project, formEntity, selection, componentToWidget, recursionGuard);
                    childrenWidgets.add(childWidget);
                    if (childWidget != null) {
                        hasChildren = true;
                    }
                }
            }
            flexPanel = hasChildren ? new LinesPanel(this, formEntity, childrenWidgets) : null;
        }

        if (flexPanel != null && flexPanel.getComponentCount() > 0) {
            JBScrollPane scrollPane = new JBScrollPane();
            scrollPane.setViewportView(flexPanel);
            return new JComponentPanel(new JComponentPanel(scrollPane));
        } else {
            return null;
        }
    }

    private boolean isSimple() {
        return lines == 1 && !isAlignCaptions();
    }

    public boolean isAlignCaptions() {
        if(!isVertical()) // later maybe it makes sense to support align captions for horizontal containers, but with no-wrap it doesn't make much sense
            return false;

        int notActions = 0;
        // only simple property draws
        for(ComponentView child : children) {
            if(!(child instanceof PropertyDrawView) || /*((PropertyDrawView) child).hasColumnGroupObjects() || */(child.autoSize/* && ((PropertyDrawView) child).isAutoDynamicHeight()*/) || child.flex > 0 || ((PropertyDrawView) child).panelCaptionVertical)
                return false;

            if(!((PropertyDrawView)child).entity.isAction)
                notActions++;
        }

        if(notActions <= 1)
            return false;

        return true;
    }

    private JComponentPanel createTabbedPanel(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget, HashSet<ComponentView> recursionGuard) {
        ComponentView selectedChild = null;
        if (oldWidget != null) {
            TabbedPane oldTabbedPane = (TabbedPane) oldWidget.getComponent(0);
            selectedChild = (ComponentView) ((JComponent) oldTabbedPane.getSelectedComponent()).getClientProperty("componentView");
        }

        JComponent selectedWidget = null;
        TabbedPane tabbedPane = new TabbedPane();
        boolean hasChildren = false;
        for (ComponentView child : getChildren()) {
            if (!recursionGuard.contains(child)) {
                recursionGuard.add(child);
                JComponentPanel childWidget = child.createWidget(project, formEntity, selection, componentToWidget, recursionGuard);
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
        }

        if (hasChildren) {
            if (selectedWidget != null) {
                tabbedPane.setSelectedComponent(selectedWidget);
            }
            return new JComponentPanel(tabbedPane);
        }
        return null;
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
            super(SwingConstants.TOP);
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
