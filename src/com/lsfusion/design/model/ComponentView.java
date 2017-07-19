package com.lsfusion.design.model;

import com.intellij.designer.model.PropertiesContainer;
import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.design.ui.JComponentPanel;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.util.*;
import java.util.List;

public abstract class ComponentView extends PropertiesContainer {
    public static final List<Property> PROPERTIES = Arrays.<Property>asList(
            new ReflectionProperty("minimumSize").setExpert(),
            new ReflectionProperty("maximumSize").setExpert(),
            new ReflectionProperty("preferredSize"),
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

    public Dimension minimumSize;
    public Dimension maximumSize;
    public Dimension preferredSize;

    public boolean autoSize = false;

    public double flex = -1;
    public FlexAlignment alignment;

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

    public void setMinimumSize(Dimension minimumSize) {
        this.minimumSize = minimumSize;
    }

    public void setMinimumHeight(int minHeight) {
        if (this.minimumSize == null) {
            this.minimumSize = new Dimension(-1, minHeight);
        } else {
            this.minimumSize.height = minHeight;
        }
    }

    public void setMinimumWidth(int minWidth) {
        if (this.minimumSize == null) {
            this.minimumSize = new Dimension(minWidth, -1);
        } else {
            this.minimumSize.width = minWidth;
        }
    }

    public void setMaximumSize(Dimension maximumSize) {
        this.maximumSize = maximumSize;
    }

    public void setMaximumHeight(int maxHeight) {
        if (this.maximumSize == null) {
            this.maximumSize = new Dimension(-1, maxHeight);
        } else {
            this.maximumSize.height = maxHeight;
        }
    }

    public void setMaximumWidth(int maxWidth) {
        if (this.maximumSize == null) {
            this.maximumSize = new Dimension(maxWidth, -1);
        } else {
            this.maximumSize.width = maxWidth;
        }
    }

    public void setPreferredSize(Dimension preferredSize) {
        this.preferredSize = preferredSize;
    }

    public void setPreferredHeight(int prefHeight) {
        if (this.preferredSize == null) {
            this.preferredSize = new Dimension(-1, prefHeight);
        } else {
            this.preferredSize.height = prefHeight;
        }
    }

    public void setPreferredWidth(int prefWidth) {
        if (this.preferredSize == null) {
            this.preferredSize = new Dimension(prefWidth, -1);
        } else {
            this.preferredSize.width = prefWidth;
        }
    }

    public void setFixedSize(Dimension size) {
        setMinimumSize(size);
        setMaximumSize(size);
        setPreferredSize(size);
    }

    public void setFixedHeight(int height) {
        setMinimumHeight(height);
        setMaximumHeight(height);
        setPreferredHeight(height);
    }

    public void setFixedWidth(int width) {
        setMinimumWidth(width);
        setMaximumWidth(width);
        setPreferredWidth(width);
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
        setAlignment(fill == 0 ? FlexAlignment.LEADING : FlexAlignment.STRETCH);
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


    public Dimension getMinimumSize() {
        return minimumSize;
    }

    public Dimension getMaximumSize() {
        return maximumSize;
    }

    public Dimension getPreferredSize() {
        if (preferredSize == null) {
            if (container != null && container.isScroll()) {
                return new Dimension(-1, 1);
            }
        }
        return preferredSize;
    }

    public boolean isAutoSize() {
        return autoSize;
    }

    public double getFlex() {
        if (flex >= 0) {
            return flex;
        }
        ContainerView container = getContainer();
        if (container != null && container.isScroll()) {
            return 1;
        }
        return 0;
    }

    public FlexAlignment getAlignment() {
        if (alignment != null) {
            return alignment;
        }
        ContainerView container = getContainer();
        if (container.isScroll() || container.isSplit()) {
            return FlexAlignment.STRETCH;
        }
        return FlexAlignment.LEADING;
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

    public JComponentPanel createWidget(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget) {
        if (forceHide || !selection.get(this)) {
            return null;
        }

        JComponentPanel oldWidget = componentToWidget.get(this);

        JComponentPanel widget = createWidgetImpl(project, selection, componentToWidget, oldWidget);
        if (widget != null) {
            Border marginBorder = BorderFactory.createEmptyBorder(marginTop, marginLeft, marginBottom, marginRight);

            if (widget.getComponent(0) instanceof DataPanelView) {
                widget.setBorder(BorderFactory.createCompoundBorder(marginBorder, widget.getBorder()));
            } else {
                widget.setBorder(marginBorder);
            }

            componentToWidget.put(this, widget);
        }
        return widget;
    }

    protected JComponentPanel createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget) {
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
