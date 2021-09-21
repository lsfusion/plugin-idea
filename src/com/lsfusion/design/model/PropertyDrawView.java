package com.lsfusion.design.model;

import com.intellij.designer.model.Property;
import com.intellij.openapi.project.Project;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.FormView;
import com.lsfusion.design.KeyStrokes;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.PropertyDrawEntity;
import com.lsfusion.design.properties.ReflectionProperty;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.design.ui.JComponentPanel;
import com.lsfusion.lang.classes.*;
import com.lsfusion.util.BaseUtils;

import javax.swing.*;
import java.awt.*;
import java.text.Format;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import static com.lsfusion.util.BaseUtils.getKeyStrokeCaption;
import static com.lsfusion.util.BaseUtils.isRedundantString;

public class PropertyDrawView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("panelCaptionAfter").setExpert(),
            new ReflectionProperty("changeOnSingleClick").setExpert(),
            new ReflectionProperty("hide").setExpert(),
            new ReflectionProperty("regexp"),
            new ReflectionProperty("regexpMessage"),
            new ReflectionProperty("pattern"),
            new ReflectionProperty("maxValue"),
            new ReflectionProperty("echoSymbols"),
            new ReflectionProperty("noSort").setExpert(),
            new ReflectionProperty("notNull"),
            new ReflectionProperty("charHeight").setExpert(),
            new ReflectionProperty("charWidth").setExpert(),
            new ReflectionProperty("valueSize").setExpert(),
            new ReflectionProperty("valueAlignment"),
            new ReflectionProperty("changeKey"),
            new ReflectionProperty("showChangeKey").setExpert(),
            new ReflectionProperty("focusable"),
            new ReflectionProperty("panelCaptionVertical"),
            new ReflectionProperty("panelCaptionAlignment"),
            new ReflectionProperty("caption"),
            new ReflectionProperty("clearText").setExpert(),
            new ReflectionProperty("notSelectAll").setExpert(),
            new ReflectionProperty("toolTip"),
            new ReflectionProperty("askConfirm"),
            new ReflectionProperty("askConfirmMessage"),
            new ReflectionProperty("panelColumnVertical"),
            new ReflectionProperty("defaultCompare")
    );

    public PropertyDrawEntity entity;

    public boolean changeOnSingleClick;
    public boolean hide;
    public String regexp;
    public String regexpMessage;
    public String pattern;
    public Long maxValue;
    public boolean echoSymbols;

    public int charHeight;
    public int charWidth;
    public Dimension valueSize;
    private Boolean valueFlex;
    
    public FlexAlignment valueAlignment;

    public KeyStroke changeKey;
    public boolean showChangeKey;

    public Boolean focusable;

    public boolean panelCaptionVertical = false;
    public Boolean panelCaptionLast;
    public FlexAlignment panelCaptionAlignment;

    public boolean panelColumnVertical;

    public String caption;
    public boolean showCaption = true;
    public boolean clearText;
    public boolean notSelectAll;
    public String toolTip;

    public boolean askConfirm;
    public String askConfirmMessage;
    
    public boolean noSort;
    public boolean notNull;
    public String defaultCompare;

    public PropertyDrawView() {
        this("");
    }

    public PropertyDrawView(PropertyDrawEntity entity) {
        this(FormView.getPropertyDrawSID(entity.sID));

        this.entity = entity;

        setFixedCharWidth(entity.fixedCharWidth);
        setCharWidth(entity.charWidth);
        setImagePath(entity.iconPath);
        setChangeKey(entity.changeKey);
        setShowChangeKey(entity.showChangeKey);
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
        return showChangeKey && changeKey != null
                ? caption + " (" + getKeyStrokeCaption(changeKey) + ")"
                : caption;
    }

    public void setChangeOnSingleClick(boolean changeOnSingleClick) {
        this.changeOnSingleClick = changeOnSingleClick;
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

    public boolean isHorizontalValueFlex() {
        if(valueFlex != null)
            return valueFlex;
        return entity.baseClass != null && entity.baseClass.isFlex();
    }

    @Override
    public double getBaseDefaultFlex(FormEntity formEntity) {
        if ((container.isHorizontal() || entity.isGrid(formEntity)) && isHorizontalValueFlex())
            return getValueWidth(new JLabel());
        return super.getBaseDefaultFlex(formEntity);
    }

    @Override
    public FlexAlignment getBaseDefaultAlignment(ContainerView container) {
        if (!container.isHorizontal() && isHorizontalValueFlex())
            return FlexAlignment.STRETCH;
        return super.getBaseDefaultAlignment(container);
    }

    public Format getFormat() {
        LSFClassSet formatClass = entity.baseClass;
        if(formatClass instanceof CustomClassSet)
            formatClass = LongClass.instance;
            
        Format result = pattern != null ? ((FormatClass) formatClass).createUserFormat(pattern) : null;
        Format defaultFormat = ((FormatClass) formatClass).getDefaultFormat();
        if (result == null)
            return defaultFormat;
        if(formatClass instanceof IntegralClass) {
            ((NumberFormat) result).setParseIntegerOnly(((NumberFormat) defaultFormat).isParseIntegerOnly());
            ((NumberFormat) result).setMaximumIntegerDigits(((NumberFormat) defaultFormat).getMaximumIntegerDigits());
            ((NumberFormat) result).setGroupingUsed(((NumberFormat) defaultFormat).isGroupingUsed());
        }
        return result;
    }


    public int getValueWidth(JComponent comp) {
        if (valueSize != null && valueSize.width > -1) {
            return valueSize.width;
        }
        FontMetrics fontMetrics = comp.getFontMetrics(getFont(comp));
        LSFClassSet baseClass = entity.baseClass;
        if(baseClass != null) {
            String widthString = null;
            if(widthString == null && charWidth != 0)
                widthString = BaseUtils.replicate('0', charWidth);
            if(widthString != null)
                return baseClass.getFullWidthString(widthString, fontMetrics);

            return baseClass.getDefaultWidth(fontMetrics, this);
        }

        return getDefaultWidth(charWidth, fontMetrics);
    }
    public int getValueHeight(JComponent comp) {
        if (valueSize != null && valueSize.height > -1) {
            return valueSize.height;
        }
        int height = entity.baseClass != null ?
                entity.baseClass.getDefaultHeight(comp.getFontMetrics(getFont(comp)), charHeight == 0 ? 1 : charHeight) :
                comp.getFontMetrics(getFont(comp)).getHeight() + 1;
        if (imagePath != null) { // предпочитаемую высоту берем исходя из размера иконки
            Icon icon = BaseUtils.loadIcon(entity.project, "/images/design/" + imagePath);
            if(icon != null)
                height = Math.max(icon.getIconHeight() + 6, height);
        }
        return height;
    }

    public void setCharHeight(int charHeight) {
        this.charHeight = charHeight;
    }

    public void setCharWidth(int charWidth) {
        this.charWidth = charWidth;
    }

    public void setFixedCharWidth(int charWidth) {
        setCharWidth(charWidth);
        if(charWidth > 0)
            valueFlex = false;
    }

    public void setValueSize(Dimension minimumSize) {
        this.valueSize = minimumSize;
    }

    public void setValueHeight(int minHeight) {
        if (this.valueSize == null) {
            this.valueSize = new Dimension(-1, minHeight);
        } else {
            this.valueSize.height = minHeight;
        }
    }

    public void setValueWidth(int minWidth) {
        if (this.valueSize == null) {
            this.valueSize = new Dimension(minWidth, -1);
        } else {
            this.valueSize.width = minWidth;
        }
    }

    private int getDefaultWidth(int minCharWidth, FontMetrics fontMetrics) {
        String minMask = minCharWidth != 0
                ? BaseUtils.replicate('0', minCharWidth)
                : "1234567";

        return fontMetrics.stringWidth(minMask) + 8;
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
    
    public FlexAlignment getValueAlignment() {
        return valueAlignment;
    }
    
    public void setValueAlignment(FlexAlignment valueAlignment) {
        this.valueAlignment = valueAlignment;
    } 

    public void setChangeKey(KeyStroke editKey) {
        this.changeKey = editKey;
    }

    public void setShowChangeKey(boolean showEditKey) {
        this.showChangeKey = showEditKey;
    }

    public void setFocusable(Boolean focusable) {
        this.focusable = focusable;
    }

    public void setPanelCaptionVertical(boolean panelCaptionVertical) {
        this.panelCaptionVertical = panelCaptionVertical;
    }

    public void setPanelCaptionLast(boolean panelCaptionLast) {
        this.panelCaptionLast = panelCaptionLast;
    }

    public void setPanelCaptionAlignment(FlexAlignment panelCaptionAlignment) {
        this.panelCaptionAlignment = panelCaptionAlignment;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

    public void setClearText(boolean clearText) {
        this.clearText = clearText;
    }

    public void setNotSelectAll(boolean notSelectAll) {
        this.notSelectAll = notSelectAll;
    }

    public void setAskConfirm(boolean askConfirm) {
        this.askConfirm = askConfirm;
    }

    public void setAskConfirmMessage(String askConfirmMessage) {
        this.askConfirmMessage = askConfirmMessage;
    }

    public void setPanelColumnVertical(boolean panelColumnVertical) {
        this.panelColumnVertical = panelColumnVertical;
    }

    public void setNoSort(boolean noSort) {
        this.noSort = noSort;
    }

    public void setDefaultCompare(String defaultCompare) {
        this.defaultCompare = defaultCompare;
    }
    
    public void setNotNull(boolean notNull) {
        this.notNull = notNull;
    }

    public void setToolTip(String toolTip) {
        this.toolTip = toolTip;
    }

    public boolean isChangeOnSingleClick() {
        return changeOnSingleClick;
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

    public int getCharHeight() {
        return charHeight;
    }

    public int getCharWidth() {
        return charWidth;
    }
    
    public Dimension getValueSize() {
        return valueSize;
    }

    public KeyStroke getChangeKey() {
        return changeKey;
    }

    public boolean isShowChangeKey() {
        return showChangeKey;
    }

    public Boolean getFocusable() {
        return focusable;
    }

    public boolean isPanelCaptionVertical() {
        return panelCaptionVertical;
    }
    
    public boolean isPanelCaptionLast() {
        return panelCaptionLast;
    }
    
    public boolean getNotNullPanelCaptionLast() {
        return panelCaptionLast != null ? panelCaptionLast : entity.baseClass instanceof LogicalClass;
    }

    public FlexAlignment getPanelCaptionAlignment() {
        return panelCaptionAlignment;
    }

    public FlexAlignment getNotNullPanelCaptionAlignment() {
        return (panelCaptionAlignment != null && panelCaptionAlignment != FlexAlignment.STRETCH) ? panelCaptionAlignment : FlexAlignment.CENTER;
    }

    public boolean isClearText() {
        return clearText;
    }

    public boolean isNotSelectAll() {
        return notSelectAll;
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

    public boolean getPanelColumnVertical() {
        return panelColumnVertical;
    }

    public boolean getNoSort() {
        return noSort;
    }

    public String getDefaultCompare() {
        return defaultCompare;
    }

    public boolean getNotNull() {
        return notNull;
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.PROPERTY;
    }

    @Override
    protected JComponentPanel createWidgetImpl(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget, HashSet<ComponentView> recursionGuard) {
        if (entity.isAction) {
            return new JComponentPanel(new ActionPanelView(project, this));
        } else {
            return new JComponentPanel(new DataPanelView(this));
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
                    "<b>Canonical name:</b> %3$s<br>" +
//                    "<b>Таблица:</b> %4$s<br>" +
                    "<b>Objects:</b> %4$s<br>" +
                    "<b>Signature:</b> %6$s (%5$s)<br>" +
                    "<b>Script:</b> %7$s<br>" +
                    "<b>Path:</b> %8$s<br>" +
                    "<hr>" +
                    "<b>Form property name:</b> %9$s" +
                    "</html>";

    public static final String DETAILED_ACTION_TOOL_TIP_FORMAT =
            "<hr>" +
                    "<b>Canonical name:</b> %3$s<br>" +
                    "<b>Objects:</b> %4$s<br>" +
                    "<b>Path:</b> %5$s<br>" +
                    "<hr>" +
                    "<b>Form property name:</b> %6$s" +
                    "</html>";

    public static final String EDIT_KEY_TOOL_TIP_FORMAT =
            "<hr><b>Hotkey:</b> %1$s<br>";

    public String getTooltipText(String caption) {
        String propCaption = BaseUtils.nullTrim(!isRedundantString(toolTip) ? toolTip : caption);
        String editKeyText = changeKey == null ? "" : String.format(EDIT_KEY_TOOL_TIP_FORMAT, KeyStrokes.getKeyStrokeCaption(changeKey));

//        String tableName = this.tableName != null ? this.tableName : "&lt;none&gt;";
        String ifaceObjects = BaseUtils.toString(", ", entity.objectClasses.toArray());
        String scriptPath = entity.declLocation != null ? entity.declLocation.replace("\n", "<br>") : "";
        
        if (entity.isAction) {
            return String.format(TOOL_TIP_FORMAT + DETAILED_ACTION_TOOL_TIP_FORMAT, propCaption, editKeyText, entity.canonicalName, ifaceObjects, scriptPath, entity.sID);
        } else {
            String ifaceClasses = BaseUtils.toString(", ", entity.interfaceClasses.toArray());
            String returnClass = "";
            if (entity.baseClass != null) {
                if (entity.baseClass instanceof DataClass) {
                    returnClass = ((DataClass) entity.baseClass).getCaption();
                } else {
                    returnClass = entity.baseClass.toString();
                }
            }
            String script = entity.declText != null ? entity.declText.replace("\n", "<br>") : "";
            
            return String.format(TOOL_TIP_FORMAT + DETAILED_TOOL_TIP_FORMAT, propCaption, editKeyText, entity.canonicalName/*, tableName*/, ifaceObjects, ifaceClasses, returnClass, script, scriptPath, entity.sID);
        }
    }
}
