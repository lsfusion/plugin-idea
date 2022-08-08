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

    public void setChangeKey(KeyStroke editKey) {
        target.changeKey = editKey;
    }

    public void setShowChangeKey(boolean showEditKey) {
        target.showChangeKey = showEditKey;
    }

    public void setChangeMouse(String changeMouse) {
        target.changeMouse = changeMouse;
    }

    public void setFocusable(Boolean focusable) {
        target.focusable = focusable;
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

    public void setCaption(String caption) {
        target.caption = caption;
    }

    public void setClearText(boolean clearText) {
        target.clearText = clearText;
    }

    public void setNotSelectAll(boolean notSelectAll) {
        target.notSelectAll = notSelectAll;
    }

    public void setAskConfirm(boolean askConfirm) {
        target.entity.askConfirm = askConfirm;
    }

    public void setAskConfirmMessage(String askConfirmMessage) {
        target.entity.askConfirmMessage = askConfirmMessage;
    }

    public void setPanelColumnVertical(boolean panelColumnVertical) {
        target.panelColumnVertical = panelColumnVertical;
    }

    public void setToolTip(String toolTip) {
        target.toolTip = toolTip;
    }

    public void setNotNull(boolean notNull) {
        target.notNull = notNull;
    }

    public void setValueSize(Dimension size) {
        target.setValueSize(size);
    }
    public void setValueHeight(int prefHeight) {
        target.setValueHeight(prefHeight);
    }
    public void setValueWidth(int prefWidth) {
        target.setValueWidth(prefWidth);
    }
    public void setCharHeight(int charHeight) {
        target.setCharHeight(charHeight);
    }
    public void setCharWidth(int charWidth) {
        target.setCharWidth(charWidth);
    }

    public void setValueFlex(boolean valueFlex) {
        target.setValueFlex(valueFlex);
    }

    public void setValueAlignment(FlexAlignment valueAlignment) {
        this.setValueAlignment(valueAlignment);
    }

    public void setBoxed(boolean boxed) {
        target.boxed = boxed;
    }
}
