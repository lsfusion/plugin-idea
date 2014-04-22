package com.lsfusion.design.model.proxy;

import com.lsfusion.design.FormView;
import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.ui.FlexAlignment;

import java.awt.*;

public class FormViewProxy extends ViewProxy<FormView> {
    private final ContainerViewProxy mainContainerProxy;

    public FormViewProxy(FormView target) {
        super(target);

        mainContainerProxy = new ContainerViewProxy(target.mainContainer);
    }

//    public void setKeyStroke(KeyStroke keyStroke) {
//        target.keyStroke = keyStroke;
//    }

    public void setCaption(String caption) {
        target.caption = caption;
    }

    public void setOverridePageWidth(Integer overridePageWidth) {
        target.overridePageWidth = overridePageWidth;
    }

    /* ========= Redirection to Main Container ========= */

    public void setMinimumSize(Dimension minimumSize) {
        mainContainerProxy.setMinimumSize(minimumSize);
    }

    public void setMinimumHeight(int minHeight) {
        mainContainerProxy.setMinimumHeight(minHeight);
    }

    public void setMinimumWidth(int minWidth) {
        mainContainerProxy.setMinimumWidth(minWidth);
    }

    public void setMaximumSize(Dimension maximumSize) {
        mainContainerProxy.setMaximumSize(maximumSize);
    }

    public void setMaximumHeight(int maxHeight) {
        mainContainerProxy.setMaximumHeight(maxHeight);
    }

    public void setMaximumWidth(int maxWidth) {
        mainContainerProxy.setMaximumWidth(maxWidth);
    }

    public void setPreferredSize(Dimension preferredSize) {
        mainContainerProxy.setPreferredSize(preferredSize);
    }

    public void setPreferredHeight(int prefHeight) {
        mainContainerProxy.setPreferredHeight(prefHeight);
    }

    public void setPreferredWidth(int prefWidth) {
        mainContainerProxy.setPreferredWidth(prefWidth);
    }

    public void setFixedSize(Dimension size) {
        mainContainerProxy.setFixedSize(size);
    }

    public void setFixedHeight(int height) {
        mainContainerProxy.setFixedHeight(height);
    }

    public void setFixedWidth(int width) {
        mainContainerProxy.setFixedWidth(width);
    }

    public void setColumns(int columns) {
        mainContainerProxy.setColumns(columns);
    }

    public void setMarginTop(int marginTop) {
        mainContainerProxy.setMarginTop(marginTop);
    }

    public void setMarginBottom(int marginBottom) {
        mainContainerProxy.setMarginBottom(marginBottom);
    }

    public void setMarginLeft(int marginLeft) {
        mainContainerProxy.setMarginLeft(marginLeft);
    }

    public void setMarginRight(int marginRight) {
        mainContainerProxy.setMarginRight(marginRight);
    }

    public void setMargin(int margin) {
        mainContainerProxy.setMargin(margin);
    }

    public void setChildrenAlignment(FlexAlignment falign) {
        mainContainerProxy.setChildrenAlignment(falign);
    }

    public void setType(ContainerType type) {
        mainContainerProxy.setType(type);
    }

    public void setDescription(String description) {
        mainContainerProxy.setDescription(description);
    }
}
