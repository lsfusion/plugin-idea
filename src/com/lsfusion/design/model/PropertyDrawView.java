package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.lang.classes.DataClass;
import com.lsfusion.util.BaseUtils;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.Map;

import static com.lsfusion.util.BaseUtils.getKeyStrokeCaption;
import static com.lsfusion.util.BaseUtils.isRedundantString;
import static java.lang.Math.max;

public class PropertyDrawView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("panelCaptionAfter").setExpert(),
            new ReflectionProperty("editOnSingleClick").setExpert(),
            new ReflectionProperty("hide").setExpert(),
            new ReflectionProperty("regexp"),
            new ReflectionProperty("regexpMessage"),
            new ReflectionProperty("pattern"),
            new ReflectionProperty("maxValue"),
            new ReflectionProperty("echoSymbols"),
            new ReflectionProperty("noSort").setExpert(),
            new ReflectionProperty("notNull"),
            new ReflectionProperty("minimumCharWidth").setExpert(),
            new ReflectionProperty("maximumCharWidth").setExpert(),
            new ReflectionProperty("preferredCharWidth"),
            new ReflectionProperty("editKey"),
            new ReflectionProperty("showEditKey").setExpert(),
            new ReflectionProperty("focusable"),
            new ReflectionProperty("autoSize"),
            new ReflectionProperty("panelCaptionAbove"),
            new ReflectionProperty("caption"),
            new ReflectionProperty("clearText").setExpert(),
            new ReflectionProperty("toolTip"),
            new ReflectionProperty("askConfirm"),
            new ReflectionProperty("askConfirmMessage")
    );

    public PropertyDrawEntity entity;

    public boolean panelCaptionAfter;
    public boolean editOnSingleClick;
    public boolean hide;
    public String regexp;
    public String regexpMessage;
    public String pattern;
    public Long maxValue;
    public boolean echoSymbols;

    public int minimumCharWidth;
    public int maximumCharWidth;
    public int preferredCharWidth;

    public KeyStroke editKey;
    public boolean showEditKey;

    public Boolean focusable;

    public boolean autoSize = false;
    public boolean panelCaptionAbove = false;

    public String caption;
    public boolean showCaption = true;
    public boolean clearText;
    public String toolTip;

    public boolean askConfirm;
    public String askConfirmMessage;
    
    public boolean noSort;
    public boolean notNull;

    public PropertyDrawView() {
        this("");
    }

    public PropertyDrawView(PropertyDrawEntity entity) {
        this(entity.sID);

        this.entity = entity;

        setFixedCharWidth(entity.fixedCharWidth);
        setMinimumCharWidth(entity.minimumCharWidth);
        setMaximumCharWidth(entity.maximumCharWidth);
        setPreferredCharWidth(entity.preferredCharWidth);
        setImagePath(entity.iconPath);
        setEditKey(entity.editKey);
        setShowEditKey(entity.showEditKey);
    }

    public PropertyDrawView(String sID) {
        super(sID);
        setMargin(2);
    }

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    @Override
    public String getDisplaySID() {
        return entity.propertyName != null ? entity.propertyName : getSID();
    }

    @Override
    public String getCaption() {
        if (caption == null) {
            String entityCaption = this.entity.getCaption();
            return entityCaption != null ? entityCaption : getDisplaySID();
        } else {
            return caption;
        }
    }

    public String getEditCaption() {
        String caption = getCaption();
        return showEditKey && editKey != null
                ? caption + " (" + getKeyStrokeCaption(editKey) + ")"
                : caption;
    }

    public void setPanelCaptionAfter(boolean panelCaptionAfter) {
        this.panelCaptionAfter = panelCaptionAfter;
    }

    public void setEditOnSingleClick(boolean editOnSingleClick) {
        this.editOnSingleClick = editOnSingleClick;
    }

    public void setHide(boolean hide) {
        this.hide = hide;
    }

    public void setRegexp(String regexp) {
        this.regexp = regexp;
    }

    public void setRegexpMessage(String regexpMessage) {
        this.regexpMessage = regexpMessage;
    }
    
    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    public void setMaxValue(long maxValue) {
        this.maxValue = maxValue;
    }

    public void setEchoSymbols(boolean echoSymbols) {
        this.echoSymbols = echoSymbols;
    }

    public void setMinimumCharWidth(int minimumCharWidth) {
        this.minimumCharWidth = minimumCharWidth;
    }

    public void setMaximumCharWidth(int maximumCharWidth) {
        this.maximumCharWidth = maximumCharWidth;
    }

    public void setPreferredCharWidth(int preferredCharWidth) {
        this.preferredCharWidth = preferredCharWidth;
    }

    public void setFixedCharWidth(int charWidth) {
        setMinimumCharWidth(charWidth);
        setMaximumCharWidth(charWidth);
        setPreferredCharWidth(charWidth);
    }

    public int getMinimumWidth(JComponent comp) {
        if (minimumSize != null && minimumSize.width > -1) {
            return minimumSize.width;
        }
        return entity.baseClass != null ?
                entity.baseClass.getMinimumWidth(minimumCharWidth, comp.getFontMetrics(getFont(comp))) :
                getDefaultMinimumWidth(minimumCharWidth, comp.getFontMetrics(getFont(comp)));
    }

    private int getDefaultMinimumWidth(int minCharWidth, FontMetrics fontMetrics) {
        String minMask = minCharWidth != 0
                ? BaseUtils.replicate('0', minCharWidth)
                : "1234567";

        return fontMetrics.stringWidth(minMask) + 8;
    }

    public int getMinimumHeight(JComponent comp) {
        if (minimumSize != null && minimumSize.height > -1) {
            return minimumSize.height;
        }
        return getPreferredHeight(comp);
    }

    public Dimension getMinimumSize(JComponent comp) {
        return new Dimension(getMinimumWidth(comp), getMinimumHeight(comp));
    }

    public int getPreferredWidth(JComponent comp) {
        if (preferredSize != null && preferredSize.width > -1) {
            return preferredSize.width;
        }
        return entity.baseClass != null ?
                entity.baseClass.getPreferredWidth(preferredCharWidth, comp.getFontMetrics(getFont(comp))) :
                getDefaultPreferredWidth(preferredCharWidth, comp.getFontMetrics(getFont(comp)));
    }

    private int getDefaultPreferredWidth(int prefCharWidth, FontMetrics fontMetrics) {
        String prefMask = prefCharWidth != 0
                ? BaseUtils.replicate('0', prefCharWidth)
                : "1234567";

        return fontMetrics.stringWidth(prefMask) + 8;
    }

    public int getPreferredHeight(JComponent comp) {
        if (preferredSize != null && preferredSize.height > -1) {
            return preferredSize.height;
        }
        int height = entity.baseClass != null ?
                entity.baseClass.getPreferredHeight(comp.getFontMetrics(getFont(comp))) :
                getDefaultPreferredHeight(comp.getFontMetrics(getFont(comp)));
        if (imagePath != null) { // предпочитаемую высоту берем исходя из размера иконки
            Icon icon = BaseUtils.loadIcon(entity.project, "/images/" + imagePath);
            height = Math.max(icon.getIconHeight() + 6, height);
        }
        return height;
    }

    private int getDefaultPreferredHeight(FontMetrics fontMetrics) {
        return fontMetrics.getHeight() + 1;
    }

    public Dimension getPreferredSize(JComponent comp) {
        return new Dimension(getPreferredWidth(comp), getPreferredHeight(comp));
    }

    public int getMaximumWidth(JComponent comp) {
        int result;
        if (maximumSize != null && maximumSize.width > -1) {
            result = maximumSize.width;
        } else {
            result = entity.baseClass != null ?
                    entity.baseClass.getMaximumWidth(maximumCharWidth, comp.getFontMetrics(getFont(comp))) :
                    getDefaultMaximumWidth(maximumCharWidth, comp.getFontMetrics(getFont(comp)));
        }
        return max(result, getPreferredWidth(comp));
    }

    private int getDefaultMaximumWidth(int maxCharWidth, FontMetrics fontMetrics) {
        if (maxCharWidth != 0)
            return fontMetrics.stringWidth(BaseUtils.replicate('0', maxCharWidth)) + 8;
        else
            return Integer.MAX_VALUE;
    }

    public int getMaximumHeight(JComponent comp) {
        int result;
        if (maximumSize != null && maximumSize.width > -1) {
            result = maximumSize.height;
        } else {
            result = entity.baseClass != null ?
                    entity.baseClass.getMaximumHeight(comp.getFontMetrics(getFont(comp))) :
                    getDefaultMaximumHeight(comp.getFontMetrics(getFont(comp)));
        }
        return max(result, getPreferredHeight(comp));
    }

    private int getDefaultMaximumHeight(FontMetrics fontMetrics) {
        return getDefaultPreferredHeight(fontMetrics);
    }

    public Dimension getMaximumSize(JComponent comp) {
        return new Dimension(getMaximumWidth(comp), getMaximumHeight(comp));
    }

    private Font getFont(Component component) {
        if (font == null) {
            return component.getFont();
        }

        Object oFont = component instanceof JComponent ? ((JComponent) component).getClientProperty(font) : null;
        if (oFont instanceof Font) {
            return (Font) oFont;
        }

        Font cFont = font.deriveFrom(component);
        if (component instanceof JComponent) {
            ((JComponent) component).putClientProperty(font, cFont);
        }
        return cFont;
    }

    public void setEditKey(KeyStroke editKey) {
        this.editKey = editKey;
    }

    public void setShowEditKey(boolean showEditKey) {
        this.showEditKey = showEditKey;
    }

    public void setFocusable(Boolean focusable) {
        this.focusable = focusable;
    }

    public void setAutoSize(boolean autoSize) {
        this.autoSize = autoSize;
    }

    public void setPanelCaptionAbove(boolean panelCaptionAbove) {
        this.panelCaptionAbove = panelCaptionAbove;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setClearText(boolean clearText) {
        this.clearText = clearText;
    }

    public void setAskConfirm(boolean askConfirm) {
        this.askConfirm = askConfirm;
    }

    public void setAskConfirmMessage(String askConfirmMessage) {
        this.askConfirmMessage = askConfirmMessage;
    }
    
    public void setNoSort(boolean noSort) {
        this.noSort = noSort;
    }
    
    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public boolean isPanelCaptionAfter() {
        return panelCaptionAfter;
    }

    public boolean isEditOnSingleClick() {
        return editOnSingleClick;
    }

    public boolean isHide() {
        return hide;
    }

    public String getRegexp() {
        return regexp;
    }

    public String getRegexpMessage() {
        return regexpMessage;
    }
    
    public String getPattern() {
        return pattern;
    }

    public Long getMaxValue() {
        return maxValue;
    }

    public boolean isEchoSymbols() {
        return echoSymbols;
    }

    public int getMinimumCharWidth() {
        return minimumCharWidth;
    }

    public int getMaximumCharWidth() {
        return maximumCharWidth;
    }

    public int getPreferredCharWidth() {
        return preferredCharWidth;
    }

    public KeyStroke getEditKey() {
        return editKey;
    }

    public boolean isShowEditKey() {
        return showEditKey;
    }

    public Boolean getFocusable() {
        return focusable;
    }

    public boolean isAutoSize() {
        return autoSize;
    }

    public boolean isPanelCaptionAbove() {
        return panelCaptionAbove;
    }

    public boolean isClearText() {
        return clearText;
    }

    public String getToolTip() {
        return toolTip;
    }

    public boolean isAskConfirm() {
        return askConfirm;
    }

    public String getAskConfirmMessage() {
        return askConfirmMessage;
    }
    
    public boolean getNoSort() {
        return noSort;
    }
    
    public boolean getNotNull() {
        return notNull;
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.PROPERTY;
    }

    @Override
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        if (entity.isAction) {
            return new ActionPanelView(project, this);
        } else {
            return new DataPanelView(this);
        }
    }

    public boolean isForcedPanel() {
        return entity.forceViewType == ClassViewType.PANEL;
    }

    public boolean isForceHide() {
        return entity.forceViewType == ClassViewType.HIDE;
    }

    public static final String TOOL_TIP_FORMAT =
            "<html><b>%1$s</b><br>" +
                    "%2$s";

    public static final String DETAILED_TOOL_TIP_FORMAT =
            "<hr>" +
                    "<b>sID:</b> %3$s<br>" +
//                    "<b>Таблица:</b> %4$s<br>" +
                    "<b>Объекты:</b> %4$s<br>" +
                    "<b>Сигнатура:</b> %6$s <i>%3$s</i> (%5$s)<br>" +
                    "<b>Скрипт:</b> %7$s<br>" +
                    "<b>Путь:</b> %8$s" +
                    "</html>";

    public static final String EDIT_KEY_TOOL_TIP_FORMAT =
            "<hr><b>Горячая клавиша:</b> %1$s<br>";

    public String getTooltipText(String caption) {
        String propCaption = BaseUtils.nullTrim(!isRedundantString(toolTip) ? toolTip : caption);
        String editKeyText = editKey == null ? "" : String.format(EDIT_KEY_TOOL_TIP_FORMAT, KeyStrokes.getKeyStrokeCaption(editKey));

        String sid = getDisplaySID();
//        String tableName = this.tableName != null ? this.tableName : "&lt;none&gt;";
        String ifaceObjects = BaseUtils.toString(", ", entity.objectClasses.toArray());
        String ifaceClasses = BaseUtils.toString(", ", entity.interfeceClasses.toArray());
        String returnClass = "";
        if (entity.baseClass != null) {
            if (entity.baseClass instanceof DataClass) {
                returnClass = ((DataClass) entity.baseClass).getCaption();
            } else {
                returnClass = entity.baseClass.toString();
            }
        }

        String script = entity.declText != null ? entity.declText.replace("\n", "<br>") : "";
        String scriptPath = entity.declLocation != null ? entity.declLocation.replace("\n", "<br>") : "";
        return String.format(TOOL_TIP_FORMAT + DETAILED_TOOL_TIP_FORMAT, propCaption, editKeyText, sid/*, tableName*/, ifaceObjects, ifaceClasses, returnClass, script, scriptPath);
    }
}
