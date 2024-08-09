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

/*adding new property:
1. add to PROPERTIES
2. create field
3. create getter if needed for old design preview (not expert)
4. create setter in Proxy*/

public class PropertyDrawView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("autoSize"),
            new ReflectionProperty("boxed"),
            new ReflectionProperty("panelCaptionVertical"),
            new ReflectionProperty("panelCaptionLast"),
            new ReflectionProperty("panelCaptionAlignment"),
            new ReflectionProperty("editOnSingleClick").setExpert(), //backward compatibility
            new ReflectionProperty("changeOnSingleClick").setExpert(),
            new ReflectionProperty("hide").setExpert(),
            new ReflectionProperty("regexp").setExpert(),
            new ReflectionProperty("regexpMessage").setExpert(),
            new ReflectionProperty("pattern").setExpert(),
            new ReflectionProperty("maxValue"),
            new ReflectionProperty("echoSymbols"),
            new ReflectionProperty("noSort").setExpert(),
            new ReflectionProperty("defaultCompare"),
            new ReflectionProperty("valueSize").setExpert(),
            new ReflectionProperty("valueHeight").setExpert(),
            new ReflectionProperty("valueWidth").setExpert(),
            new ReflectionProperty("captionHeight"),
            new ReflectionProperty("captionWidth"),
            new ReflectionProperty("charHeight").setExpert(),
            new ReflectionProperty("charWidth").setExpert(),
            new ReflectionProperty("valueFlex"),
            new ReflectionProperty("changeKey"),
            new ReflectionProperty("changeKeyPriority"),
            new ReflectionProperty("changeMouse"),
            new ReflectionProperty("changeMousePriority"),
            new ReflectionProperty("showChangeKey").setExpert(),
            new ReflectionProperty("focusable"),
            new ReflectionProperty("panelColumnVertical"),
            new ReflectionProperty("valueClass").setExpert(),
            new ReflectionProperty("captionClass").setExpert(),
            new ReflectionProperty("caption"),
            new ReflectionProperty("tag").setExpert(),
            new ReflectionProperty("inputType").setExpert(),
            new ReflectionProperty("imagePath").setExpert(), //backward compatibility
            new ReflectionProperty("image"),
            new ReflectionProperty("comment"),
            new ReflectionProperty("commentClass").setExpert(),
            new ReflectionProperty("panelCommentVertical"),
            new ReflectionProperty("panelCommentFirst"),
            new ReflectionProperty("panelCommentAlignment"),
            new ReflectionProperty("placeholder"),
            new ReflectionProperty("toolTip").setExpert(), //deprecated
            new ReflectionProperty("tooltip"),
            new ReflectionProperty("valueTooltip"),
            new ReflectionProperty("valueAlignment"), //deprecated
            new ReflectionProperty("valueAlignmentHorz").setExpert(),
            new ReflectionProperty("valueAlignmentVert").setExpert(),
            new ReflectionProperty("valueOverflowHorz").setExpert(),
            new ReflectionProperty("valueOverflowVert").setExpert(),
            new ReflectionProperty("valueShrinkHorz").setExpert(),
            new ReflectionProperty("valueShrinkVert").setExpert(),
            new ReflectionProperty("clearText").setExpert(),
            new ReflectionProperty("notSelectAll").setExpert(),
            new ReflectionProperty("askConfirm").setExpert(),
            new ReflectionProperty("askConfirmMessage").setExpert(),
            new ReflectionProperty("toolbar"),
            new ReflectionProperty("notNull"),
            new ReflectionProperty("select").setExpert()
    );

    @Override
    public List<Property> getProperties() {
        return PROPERTIES;
    }

    public PropertyDrawEntity entity;

    public boolean autoSize;

    public boolean boxed;

    public boolean panelCaptionVertical;
    public Boolean panelCaptionLast;
    public FlexAlignment panelCaptionAlignment;

    public boolean changeOnSingleClick;
    public boolean hide;
    public String regexp;
    public String regexpMessage;
    public String pattern;
    public Long maxValue;
    public boolean echoSymbols;

    public boolean noSort;
    public String defaultCompare;

    public Dimension valueSize;

    public int captionHeight;
    public int captionWidth;

    public int charHeight;
    public int charWidth;

    public Boolean valueFlex;

    public KeyStroke changeKey;
    public int changeKeyPriority;
    public String changeMouse;
    public int changeMousePriority;
    public boolean showChangeKey;

    public boolean focusable;

    public boolean panelColumnVertical;

    public String valueClass;
    public String captionClass;
    public String caption;

    public String tag;
    public String inputType;

    public String image;

    public String comment;
    public String commentClass;
    public boolean panelCommentVertical;
    public Boolean panelCommentFirst;
    public FlexAlignment panelCommentAlignment;

    public String placeholder;

    public String tooltip;
    public String valueTooltip;

    public FlexAlignment valueAlignmentHorz;
    public FlexAlignment valueAlignmentVert;
    public String valueOverflowHorz;
    public String valueOverflowVert;
    public boolean valueShrinkHorz;
    public boolean valueShrinkVert;

    public boolean clearText;
    public boolean notSelectAll;

    public boolean askConfirm;
    public String askConfirmMessage;

    public boolean toolbar;

    public boolean notNull;

    public String select;

    public boolean showCaption = true;


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

    @SuppressWarnings("unused")
    public boolean isAutoSize() {
        return autoSize;
    }

    @SuppressWarnings("unused")
    public boolean isBoxed() {
        return boxed;
    }

    @SuppressWarnings("unused")
    public boolean isPanelCaptionVertical() {
        return panelCaptionVertical;
    }

    @SuppressWarnings("unused")
    public Boolean getPanelCaptionLast() {
        return panelCaptionLast;
    }

    @SuppressWarnings("unused")
    public FlexAlignment getPanelCaptionAlignment() {
        return panelCaptionAlignment;
    }

    @SuppressWarnings("unused")
    public Long getMaxValue() {
        return maxValue;
    }

    @SuppressWarnings("unused")
    public boolean isEchoSymbols() {
        return echoSymbols;
    }

    @SuppressWarnings("unused")
    public String getDefaultCompare() {
        return defaultCompare;
    }

    @SuppressWarnings("unused")
    public int getCaptionHeight() {
        return captionHeight;
    }

    @SuppressWarnings("unused")
    public int getCaptionWidth() {
        return captionWidth;
    }

    public void setCharWidth(int charWidth) {
        this.charWidth = charWidth;
    }

    @SuppressWarnings("unused")
    public Boolean getValueFlex() {
        return valueFlex;
    }

    @SuppressWarnings("unused")
    public KeyStroke getChangeKey() {
        return changeKey;
    }

    public void setChangeKey(KeyStroke changeKey) {
        this.changeKey = changeKey;
    }

    @SuppressWarnings("unused")
    public int getChangeKeyPriority() {
        return changeKeyPriority;
    }

    @SuppressWarnings("unused")
    public String getChangeMouse() {
        return changeMouse;
    }

    @SuppressWarnings("unused")
    public int getChangeMousePriority() {
        return changeMousePriority;
    }

    public void setShowChangeKey(boolean showChangeKey) {
        this.showChangeKey = showChangeKey;
    }

    @SuppressWarnings("unused")
    public boolean isFocusable() {
        return focusable;
    }

    @SuppressWarnings("unused")
    public boolean isPanelColumnVertical() {
        return panelColumnVertical;
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

    public String getImage() {
        return image;
    }

    @SuppressWarnings("unused")
    public String getComment() {
        return comment;
    }

    @SuppressWarnings("unused")
    public boolean isPanelCommentVertical() {
        return panelCommentVertical;
    }

    @SuppressWarnings("unused")
    public Boolean getPanelCommentFirst() {
        return panelCommentFirst;
    }

    @SuppressWarnings("unused")
    public FlexAlignment getPanelCommentAlignment() {
        return panelCommentAlignment;
    }

    @SuppressWarnings("unused")
    public String getPlaceholder() {
        return placeholder;
    }

    @SuppressWarnings("unused")
    public String getTooltip() {
        return tooltip;
    }

    @SuppressWarnings("unused")
    public String getValueTooltip() {
        return valueTooltip;
    }

    @SuppressWarnings("unused")
    public FlexAlignment getValueAlignment() {
        return valueAlignmentHorz;
    }

    @SuppressWarnings("unused")
    public boolean isToolbar() {
        return toolbar;
    }

    @SuppressWarnings("unused")
    public boolean isNotNull() {
        return notNull;
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
            if(charWidth != 0)
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

    public void setFixedCharWidth(int charWidth) {
        setCharWidth(charWidth);
        if(charWidth > 0)
            valueFlex = false;
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
    
    public boolean getNotNullPanelCaptionLast() {
        return panelCaptionLast != null ? panelCaptionLast : entity.baseClass instanceof LogicalClass;
    }

    public FlexAlignment getNotNullPanelCaptionAlignment() {
        return (panelCaptionAlignment != null && panelCaptionAlignment != FlexAlignment.STRETCH) ? panelCaptionAlignment : FlexAlignment.CENTER;
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
        String propCaption = BaseUtils.nullTrim(!isRedundantString(tooltip) ? tooltip : caption);
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
