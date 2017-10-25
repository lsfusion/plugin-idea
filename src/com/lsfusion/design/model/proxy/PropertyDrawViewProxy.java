package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.PropertyDrawView;

import javax.swing.*;
import java.awt.*;

@SuppressWarnings("unused")
public class PropertyDrawViewProxy extends ComponentViewProxy<PropertyDrawView> {

    public PropertyDrawViewProxy(PropertyDrawView target) {
        super(target);
    }

    public void setPanelCaptionAfter(boolean panelCaptionAfter) {
        target.panelCaptionAfter = panelCaptionAfter;
    }

    public void setEditOnSingleClick(boolean editOnSingleClick) {
        target.editOnSingleClick = editOnSingleClick;
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

    public void setMinimumCharWidth(int minimumCharWidth) {
        target.setMinimumCharWidth(minimumCharWidth);
    }

    public void setMaximumCharWidth(int maximumCharWidth) {
        target.setMaximumCharWidth(maximumCharWidth);
    }

    public void setPreferredCharWidth(int preferredCharWidth) {
        target.setPreferredCharWidth(preferredCharWidth);
    }

    public void setMinimumValueSize(Dimension minimumSize) {
        target.setMinimumValueSize(minimumSize);
    }

    public void setMinimumValueHeight(int minHeight) {
        target.setMinimumValueHeight(minHeight);
    }

    public void setMinimumValueWidth(int minWidth) {
        target.setMinimumValueWidth(minWidth);
    }

    public void setMaximumValueSize(Dimension maximumSize) {
        target.setMaximumValueSize(maximumSize);
    }

    public void setMaximumValueHeight(int maxHeight) {
        target.setMaximumValueHeight(maxHeight);
    }

    public void setMaximumValueWidth(int maxWidth) {
        target.setMaximumValueWidth(maxWidth);
    }

    public void setPreferredValueSize(Dimension preferredSize) {
        target.setPreferredValueSize(preferredSize);
    }

    public void setPreferredValueHeight(int prefHeight) {
        target.setPreferredValueHeight(prefHeight);
    }

    public void setPreferredValueWidth(int prefWidth) {
        target.setPreferredValueWidth(prefWidth);
    }

    public void setFixedValueSize(Dimension size) {
        setMinimumValueSize(size);
        setMaximumValueSize(size);
        setPreferredValueSize(size);
    }

    public void setFixedValueHeight(int height) {
        setMinimumValueHeight(height);
        setMaximumValueHeight(height);
        setPreferredValueHeight(height);
    }

    public void setFixedValueWidth(int width) {
        setMinimumValueWidth(width);
        setMaximumValueWidth(width);
        setPreferredValueWidth(width);
    }
    
    public void setChangeKey(KeyStroke editKey) {
        target.changeKey = editKey;
    }

    public void setShowChangeKey(boolean showEditKey) {
        target.showChangeKey = showEditKey;
    }

    public void setFocusable(Boolean focusable) {
        target.focusable = focusable;
    }

    public void setPanelCaptionAbove(boolean panelCaptionAbove) {
        target.panelCaptionAbove = panelCaptionAbove;
    }

    public void setCaption(String caption) {
        target.caption = caption;
    }

    public void setClearText(boolean clearText) {
        target.clearText = clearText;
    }

    public void setAskConfirm(boolean askConfirm) {
        target.entity.askConfirm = askConfirm;
    }

    public void setAskConfirmMessage(String askConfirmMessage) {
        target.entity.askConfirmMessage = askConfirmMessage;
    }

    public void setToolTip(String toolTip) {
        target.toolTip = toolTip;
    }

    public void setNotNull(boolean notNull) {
        target.notNull = notNull;
    }
}
