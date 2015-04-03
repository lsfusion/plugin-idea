package com.lsfusion.design.model;

import com.intellij.designer.model.PropertiesContainer;
import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.components.JBPanel;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.FlexAlignment;
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

    public double flex = 0;
    public FlexAlignment alignment = FlexAlignment.LEADING;

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

    public ContainerView parent;

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

    public ContainerView getParent() {
        return parent;
    }

    public void setParent(ContainerView parent) {
        this.parent = parent;
    }

    public boolean removeFromParent() {
        return parent != null && parent.remove(this);
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
        return preferredSize;
    }

    public double getFlex() {
        return flex;
    }

    public FlexAlignment getAlignment() {
        return alignment;
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

    public JComponent createWidget(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget) {
        if (forceHide || !selection.get(this)) {
            return null;
        }

        JComponent oldPanel = componentToWidget.get(this);
        JComponent oldWidget = oldPanel == null ? null : (JComponent) oldPanel.getComponent(0);

        JComponent widget = createWidgetImpl(project, selection, componentToWidget, oldWidget);
        if (widget == null) {
            return null;
        }

        JComponent result = widget;
        Border marginBorder = BorderFactory.createEmptyBorder(marginTop, marginLeft, marginBottom, marginRight);

        if (widget instanceof DataPanelView) {
            result.setBorder(BorderFactory.createCompoundBorder(marginBorder, widget.getBorder()));
        } else {
            result = new JBPanel(new BorderLayout());
            result.setBorder(marginBorder);
            result.add(widget);
        }

        componentToWidget.put(this, result);
        return result;
    }

    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        JLabel jLabel = new JLabel(getClass().getSimpleName());
        jLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 1));
        return jLabel;
    }

    public static <T> List<T> addToList(@NotNull List<T> list, T... rest) {
        List<T> merged = new ArrayList<T>(list);
        Collections.addAll(merged, rest);
        return merged;
    }
}
