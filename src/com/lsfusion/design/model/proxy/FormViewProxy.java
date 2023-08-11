package com.lsfusion.design.model.proxy;

import com.lsfusion.design.FormView;
import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.ui.FlexAlignment;

import java.awt.*;

@SuppressWarnings("unused")
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

    public void setSize(Dimension size) {
        mainContainerProxy.setSize(size);
    }

    public void setHeight(int height) {
        mainContainerProxy.setHeight(height);
    }

    public void setWidth(int width) {
        mainContainerProxy.setWidth(width);
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
}
