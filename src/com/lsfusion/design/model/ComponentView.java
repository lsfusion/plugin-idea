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

public abstract class ComponentView extends PropertiesContainer {
    public static final List<Property> PROPERTIES = Arrays.<Property>asList(
            new ReflectionProperty("size"),
            new ReflectionProperty("autoSize"),
            new ReflectionProperty("flex"),
            new ReflectionProperty("alignment"),
            new ReflectionProperty("marginTop").setExpert(),
            new ReflectionProperty("marginBottom").setExpert(),
            new ReflectionProperty("marginLeft").setExpert(),
            new ReflectionProperty("marginRight").setExpert(),
            new ReflectionProperty("defaultComponent").setExpert(),
            new ReflectionProperty("font").setExpert(),
            new ReflectionProperty("captionFont").setExpert(),
            new ReflectionProperty("background"),
            new ReflectionProperty("foreground"),
            new ReflectionProperty("imagePath").setExpert()
    );
    
    private String sID;

    public Dimension size;

    public boolean autoSize = false;

    protected double flex = -1;
    protected FlexAlignment alignment;

    public int marginTop;
    public int marginBottom;
    public int marginLeft;
    public int marginRight;

    public boolean defaultComponent = false;

    public FontInfo font;

    public FontInfo captionFont;

    public Color background;
    public Color foreground;

    public String imagePath;

    public ContainerView container;

    public boolean forceHide = false;

    public ComponentView() {
        this("");
    }

    public ComponentView(String sID) {
        this.sID = sID;
    }

    public String getDisplaySID() {
        return getSID();
    }

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

    public boolean removeFromParent() {
        return container != null && container.remove(this);
    }

    public List<Property> getProperties() {
        return PROPERTIES;
    }

    public void setSize(Dimension size) {
        this.size = size;
    }

    public void setHeight(int prefHeight) {
        if (this.size == null) {
            this.size = new Dimension(-1, prefHeight);
        } else {
            this.size.height = prefHeight;
        }
    }

    public void setWidth(int prefWidth) {
        if (this.size == null) {
            this.size = new Dimension(prefWidth, -1);
        } else {
            this.size.width = prefWidth;
        }
    }

    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
    }

    public void setDefaultComponent(boolean defaultComponent) {
        this.defaultComponent = defaultComponent;
    }

    /* ========= constraints properties ========= */

    public void setFill(double fill) {
        setFlex(fill);
        setAlignment(fill == 0 ? FlexAlignment.START : FlexAlignment.STRETCH);
    }

    public void setFlex(double flex) {
        this.flex = flex;
    }

    public void setAlign(FlexAlignment alignment) {
        setAlignment(alignment);
    }

    public void setAlignment(FlexAlignment alignment) {
        this.alignment = alignment;
    }

    public void setMargin(int margin) {
        setMarginTop(margin);
        setMarginBottom(margin);
        setMarginLeft(margin);
        setMarginRight(margin);
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

    /* ========= design properties ========= */

    public void setCaptionFont(FontInfo captionFont) {
        this.captionFont = captionFont;
    }

    public void setFont(FontInfo font) {
        this.font = font;
    }

    public void setFontSize(int fontSize) {
        FontInfo font = this.font != null ? this.font.derive(fontSize) : new FontInfo(fontSize);
        setFont(font);
    }

    public void setFontStyle(String fontStyle) {
        boolean bold;
        boolean italic;
        //чтобы не заморачиваться с лишним типом для стиля просто перечисляем все варианты...
        if ("bold".equals(fontStyle)) {
            bold = true;
            italic = false;
        } else if ("italic".equals(fontStyle)) {
            bold = false;
            italic = true;
        } else if ("bold italic".equals(fontStyle) || "italic bold".equals(fontStyle)) {
            bold = true;
            italic = true;
        } else if ("".equals(fontStyle)) {
            bold = false;
            italic = false;
        } else {
            throw new IllegalArgumentException("fontStyle value must be a combination of strings bold and italic");
        }

        FontInfo font = this.font != null ? this.font.derive(bold, italic) : new FontInfo(bold, italic);
        setFont(font);
    }

    public void setBackground(Color background) {
        this.background = background;
    }

    public void setForeground(Color foreground) {
        this.foreground = foreground;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }


    public Dimension getSize() {
        return size;
    }

    public boolean isAutoSize() {
        return autoSize;
    }
    
    public double getFlex() { // нужно для проставления в Design в блоке свойства (используется через reflection)
        return getFlex(null);
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
            if (container.isTabbedPane()) {
                return 1;
            }
        return getBaseDefaultFlex(formEntity);
    }
    public double getBaseDefaultFlex(FormEntity formEntity) {
        return 0;
    }

    public FlexAlignment getAlignment() {
        if (alignment != null) {
            return alignment;
        }

        ContainerView container = getContainer();
        if (container != null && container.isTabbedPane())
            return FlexAlignment.STRETCH;
        return getBaseDefaultAlignment(container);
    }

    public FlexAlignment getBaseDefaultAlignment(ContainerView container) {
        return FlexAlignment.START;
    }

    public int getMarginTop() {
        return marginTop;
    }

    public int getMarginBottom() {
        return marginBottom;
    }

    public int getMarginLeft() {
        return marginLeft;
    }

    public int getMarginRight() {
        return marginRight;
    }

    public boolean isDefaultComponent() {
        return defaultComponent;
    }

    public FontInfo getFont() {
        return font;
    }

    public FontInfo getCaptionFont() {
        return captionFont;
    }

    public Color getBackground() {
        return background;
    }

    public Color getForeground() {
        return foreground;
    }

    public String getImagePath() {
        return imagePath;
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
        if (forceHide || !selection.get(this)) {
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

    public static <T> List<T> addToList(@NotNull List<T> list, T... rest) {
        List<T> merged = new ArrayList<>(list);
        Collections.addAll(merged, rest);
        return merged;
    }
}
