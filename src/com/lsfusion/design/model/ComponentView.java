package com.lsfusion.design.model;

import com.intellij.designer.model.PropertiesContainer;
import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.design.ui.JComponentPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.List;
import java.util.*;

/*adding new property:
1. add to PROPERTIES
2. create field
3. create setter in Proxy*/

public abstract class ComponentView extends PropertiesContainer {
    public static final List<Property> PROPERTIES = Arrays.asList(
            new ReflectionProperty("span"),
            new ReflectionProperty("defaultComponent"),
            new ReflectionProperty("activated"),
            new ReflectionProperty("fill"),
            new ReflectionProperty("size"),
            new ReflectionProperty("height"),
            new ReflectionProperty("width"),
            new ReflectionProperty("flex"),
            new ReflectionProperty("shrink"),
            new ReflectionProperty("alignShrink"),
            new ReflectionProperty("align"),
            new ReflectionProperty("alignment"),
            new ReflectionProperty("alignCaption"),
            new ReflectionProperty("marginTop"),
            new ReflectionProperty("marginBottom"),
            new ReflectionProperty("marginLeft"),
            new ReflectionProperty("marginRight"),
            new ReflectionProperty("margin"),
            new ReflectionProperty("captionFont"),
            new ReflectionProperty("font"),
            new ReflectionProperty("class"),
            new ReflectionProperty("fontSize"),
            new ReflectionProperty("fontStyle"),
            new ReflectionProperty("background"),
            new ReflectionProperty("foreground"),
            new ReflectionProperty("imagePath"), //removed in v6
            new ReflectionProperty("showIf")
    );

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    public int span;

    public boolean defaultComponent;

    public boolean activated;

    public Dimension size;

    public double flex = -1;

    public boolean shrink;

    public boolean alignShrink;

    public FlexAlignment alignment;

    public boolean alignCaption;

    public int marginTop;
    public int marginBottom;
    public int marginLeft;
    public int marginRight;

    public FontInfo captionFont;
    public FontInfo font;

    public String elementClass;

    public Color background;
    public Color foreground;

    public String imagePath;

    public boolean showIf;

    public ComponentView() {
        this("");
    }

    public ComponentView(String sID) {
        this.sID = sID;
    }

    public Dimension getSize() {
        return size;
    }

    public double getFlex(FormEntity formEntity) {
        if (flex >= 0) {
            return flex;
        }
        return getDefaultFlex(formEntity); // тут в верхней
    }

    public double getDefaultFlex(FormEntity formEntity) {
        ContainerView container = getContainer();
        if (container != null)
            if (container.isTabbed()) {
                return 1;
            }
        return getBaseDefaultFlex(formEntity);
    }

    public double getBaseDefaultFlex(FormEntity formEntity) {
        return 0;
    }

    public void setFlex(double flex) {
        this.flex = flex;
    }

    public FlexAlignment getAlignment() {
        if (alignment != null) {
            return alignment;
        }

        ContainerView container = getContainer();
        if (container != null && container.isTabbed())
            return FlexAlignment.STRETCH;
        return getBaseDefaultAlignment(container);
    }

    public FlexAlignment getBaseDefaultAlignment(ContainerView container) {
        return FlexAlignment.START;
    }

    public void setAlignment(FlexAlignment alignment) {
        this.alignment = alignment;
    }

    public void setMarginTop(int marginTop) {
        this.marginTop = marginTop;
    }

    public void setMarginBottom(int marginBottom) {
        this.marginBottom = marginBottom;
    }

    public void setMarginLeft(int marginLeft) {
        this.marginLeft = marginLeft;
    }

    public void setMarginRight(int marginRight) {
        this.marginRight = marginRight;
    }

    public void setMargin(int margin) {
        setMarginTop(margin);
        setMarginBottom(margin);
        setMarginLeft(margin);
        setMarginRight(margin);
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    protected static <T> List<T> addToList(@NotNull List<T> list, T... rest) {
        List<T> merged = new ArrayList<>(list);
        Collections.addAll(merged, rest);
        return merged;
    }

    public void decorateTreeRenderer(SimpleColoredComponent renderer) {
        String className = getClass().getSimpleName();
        String componentType = className.substring(0, className.length() - 4);

        renderer.append(componentType, SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);
        renderer.append(" (" + sID + ")", SimpleTextAttributes.REGULAR_ATTRIBUTES);

        renderer.setIcon(getIcon());
    }

    public abstract String getCaption();

    public abstract Icon getIcon();

    public JComponentPanel createWidget(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, HashSet<ComponentView> recursionGuard) {
        if (!selection.get(this)) {
            return null;
        }

        JComponentPanel oldWidget = componentToWidget.get(this);

        JComponentPanel widget = createWidgetImpl(project, formEntity, selection, componentToWidget, oldWidget, recursionGuard);
        if (widget != null) {
            Border marginBorder = BorderFactory.createEmptyBorder(marginTop, marginLeft, marginBottom, marginRight);
            widget.setBorder(BorderFactory.createCompoundBorder(marginBorder, widget.getBorder()));

            componentToWidget.put(this, widget);
        }
        return widget;
    }

    protected JComponentPanel createWidgetImpl(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget,
                                               JComponentPanel oldWidget, HashSet<ComponentView> recursionGuard) {

        JLabel jLabel = new JLabel(getClass().getSimpleName());
        jLabel.setBorder(BorderFactory.createLineBorder(JBColor.BLACK, 1));
        JComponentPanel panel = new JComponentPanel(new BorderLayout());
        panel.add(jLabel);
        return panel;
    }

    private String sID;
    public ContainerView container;

    public String getSID() {
        return sID;
    }

    public void setSID(String sID) {
        this.sID = sID;
    }

    public ContainerView getContainer() {
        return container;
    }

    public void setContainer(ContainerView container) {
        this.container = container;
    }

    public void removeFromParent() {
        if(container != null) {
            container.remove(this);
        }
    }
}
