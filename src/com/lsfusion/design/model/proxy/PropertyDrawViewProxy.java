package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.PropertyDrawView;
import com.lsfusion.design.ui.FlexAlignment;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class PropertyDrawViewProxy extends ComponentViewProxy<PropertyDrawView> {

    public PropertyDrawViewProxy(PropertyDrawView target) {
        super(target);
    }

    public void setAutoSize(boolean autoSize) {
        target.autoSize = autoSize;
    }

    public void setBoxed(boolean boxed) {
        target.boxed = boxed;
    }

    public void setPanelCaptionVertical(boolean panelCaptionVertical) {
        target.panelCaptionVertical = panelCaptionVertical;
    }

    public void setPanelCaptionLast(boolean panelCaptionLast) {
        target.panelCaptionLast = panelCaptionLast;
    }

    public void setPanelCaptionAlignment(FlexAlignment panelCaptionAlignment) {
        target.panelCaptionAlignment = panelCaptionAlignment;
    }

    public void setEditOnSingleClick(boolean editOnSingleClick) {
        setChangeOnSingleClick(editOnSingleClick);
    }

    public void setChangeOnSingleClick(boolean changeOnSingleClick) {
        target.changeOnSingleClick = changeOnSingleClick;
    }

    public void setHide(boolean hide) {
        target.hide = hide;
    }

    public void setRegexp(String regexp) {
        target.regexp = regexp;
    }

    public void setRegexpMessage(String regexpMessage) {
        target.regexpMessage = regexpMessage;
    }

    public void setPattern(String pattern) {
        target.pattern = pattern;
    }

    public void setMaxValue(long maxValue) {
        target.maxValue = maxValue;
    }

    public void setEchoSymbols(boolean echoSymbols) {
        target.echoSymbols = echoSymbols;
    }

    public void setNoSort(boolean noSort) {
        target.noSort = noSort;
    }

    public void setDefaultCompare(String defaultCompare) {
        target.defaultCompare = defaultCompare;
    }

    public void setValueSize(Dimension valueSize) {
        target.valueSize = valueSize;
    }

    public void setValueHeight(int valueHeight) {
        if (target.valueSize == null) {
            target.valueSize = new Dimension(-1, valueHeight);
        } else {
            target.valueSize.height = valueHeight;
        }
    }

    public void setValueWidth(int valueWidth) {
        if (target.valueSize == null) {
            target.valueSize = new Dimension(valueWidth, -1);
        } else {
            target.valueSize.width = valueWidth;
        }
    }

    public void setCaptionHeight(int captionHeight) {
        target.captionHeight = captionHeight;
    }

    public void setCaptionWidth(int captionWidth) {
        target.captionWidth = captionWidth;
    }

    public void setCharHeight(int charHeight) {
        target.charHeight = charHeight;
    }

    public void setCharWidth(int charWidth) {
        target.setCharWidth(charWidth);
    }

    public void setValueFlex(Boolean valueFlex) {
        target.valueFlex = valueFlex;
    }

    public void setChangeKey(KeyStroke changeKey) {
        target.setChangeKey(changeKey);
    }

    public void setChangeKeyPriority(int changeKeyPriority) {
        target.changeKeyPriority = changeKeyPriority;
    }

    public void setChangeMouse(String changeMouse) {
        target.changeMouse = changeMouse;
    }

    public void setChangeMousePriority(int changeMousePriority) {
        target.changeMousePriority = changeMousePriority;
    }

    public void setShowChangeKey(boolean showChangeKey) {
        target.setShowChangeKey(showChangeKey);
    }

    public void setFocusable(boolean focusable) {
        target.focusable = focusable;
    }

    public void setPanelColumnVertical(boolean panelColumnVertical) {
        target.panelColumnVertical = panelColumnVertical;
    }

    public void setValueClass(String valueClass) {
        target.valueClass = valueClass;
    }

    public void setCaptionClass(String captionClass) {
        target.captionClass = captionClass;
    }

    public void setCaption(String caption) {
        target.caption = caption;
    }

    public void setTag(String tag) {
        target.tag = tag;
    }

    public void setInputType(String inputType) {
        target.inputType = inputType;
    }

    public void setImagePath(String imagePath) {
        setImage(imagePath);
    }

    public void setImage(String image) {
        target.image = image;
    }

    public void setComment(String comment) {
        target.comment = comment;
    }

    public void setCommentClass(String commentClass) {
        target.commentClass = commentClass;
    }

    public void setPanelCommentVertical(boolean panelCommentVertical) {
        target.panelCommentVertical = panelCommentVertical;
    }

    public void setPanelCommentFirst(boolean panelCommentFirst) {
        target.panelCommentFirst = panelCommentFirst;
    }

    public void setPanelCommentAlignment(FlexAlignment panelCommentAlignment) {
        target.panelCommentAlignment = panelCommentAlignment;
    }

    public void setPlaceholder(String placeholder) {
        target.placeholder = placeholder;
    }

    public void setToolTip(String toolTip) {
        setTooltip(toolTip);
    }

    public void setTooltip(String tooltip) {
        target.tooltip = tooltip;
    }

    public void setValueTooltip(String valueTooltip) {
        target.valueTooltip = valueTooltip;
    }

    public void setValueAlignment(FlexAlignment valueAlignment) {
        target.valueAlignment = valueAlignment;
    }

    public void setClearText(boolean clearText) {
        target.clearText = clearText;
    }

    public void setNotSelectAll(boolean notSelectAll) {
        target.notSelectAll = notSelectAll;
    }

    public void setAskConfirm(boolean askConfirm) {
        target.askConfirm = askConfirm;
    }

    public void setAskConfirmMessage(String askConfirmMessage) {
        target.askConfirmMessage = askConfirmMessage;
    }

    public void setToolbar(boolean toolbar) {
        target.toolbar = toolbar;
    }

    public void setNotNull(boolean notNull) {
        target.notNull = notNull;
    }

    public void setSelect(String select) {
        target.select = select;
    }
}
