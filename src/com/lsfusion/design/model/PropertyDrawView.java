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
3. create setter in Proxy*/

public class PropertyDrawView extends ComponentView {
    public static final List<Property> PROPERTIES = addToList(
            ComponentView.PROPERTIES,
            new ReflectionProperty("autoSize"),
            new ReflectionProperty("boxed"),
            new ReflectionProperty("panelCaptionVertical"),
            new ReflectionProperty("panelCaptionLast"),
            new ReflectionProperty("panelCaptionAlignment"),
            new ReflectionProperty("editOnSingleClick"), //backward compatibility
            new ReflectionProperty("changeOnSingleClick"),
            new ReflectionProperty("hide"),
            new ReflectionProperty("regexp"),
            new ReflectionProperty("regexpMessage"),
            new ReflectionProperty("pattern"),
            new ReflectionProperty("maxValue"),
            new ReflectionProperty("echoSymbols"),
            new ReflectionProperty("noSort"),
            new ReflectionProperty("defaultCompare"),
            new ReflectionProperty("valueSize"),
            new ReflectionProperty("valueHeight"),
            new ReflectionProperty("valueWidth"),
            new ReflectionProperty("captionHeight"),
            new ReflectionProperty("captionWidth"),
            new ReflectionProperty("charHeight"),
            new ReflectionProperty("charWidth"),
            new ReflectionProperty("valueFlex"),
            new ReflectionProperty("changeKey"),
            new ReflectionProperty("changeKeyPriority"),
            new ReflectionProperty("changeMouse"),
            new ReflectionProperty("changeMousePriority"),
            new ReflectionProperty("showChangeKey"),
            new ReflectionProperty("focusable"),
            new ReflectionProperty("panelColumnVertical"),
            new ReflectionProperty("valueClass"),
            new ReflectionProperty("captionClass"),
            new ReflectionProperty("caption"),
            new ReflectionProperty("imagePath"), //backward compatibility
            new ReflectionProperty("image"),
            new ReflectionProperty("comment"),
            new ReflectionProperty("commentClass"),
            new ReflectionProperty("panelCommentVertical"),
            new ReflectionProperty("panelCommentFirst"),
            new ReflectionProperty("panelCommentAlignment"),
            new ReflectionProperty("placeholder"),
            new ReflectionProperty("valueAlignment"),
            new ReflectionProperty("clearText"),
            new ReflectionProperty("notSelectAll"),
            new ReflectionProperty("askConfirm"),
            new ReflectionProperty("askConfirmMessage"),
            new ReflectionProperty("toolTip"),
            new ReflectionProperty("toolbar"),
            new ReflectionProperty("notNull"),
            new ReflectionProperty("select")
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

    public String image;

    public String comment;
    public String commentClass;
    public boolean panelCommentVertical;
    public Boolean panelCommentFirst;
    public FlexAlignment panelCommentAlignment;

    public String placeholder;

    public FlexAlignment valueAlignment;
    public boolean clearText;
    public boolean notSelectAll;

    public boolean askConfirm;
    public String askConfirmMessage;

    public String toolTip;

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

    public void setCharWidth(int charWidth) {
        this.charWidth = charWidth;
    }

    public void setChangeKey(KeyStroke changeKey) {
        this.changeKey = changeKey;
    }

    public void setShowChangeKey(boolean showChangeKey) {
        this.showChangeKey = showChangeKey;
    }

    @Override
    public String getCaption() {
        if (caption == null) {
            String entityCaption = this.entity.getCaption();
            return entityCaption != null ? entityCaption : getSID();
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
