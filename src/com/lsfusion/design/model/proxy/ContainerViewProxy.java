package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

public class ContainerViewProxy extends ComponentViewProxy<ContainerView> {

    public ContainerViewProxy(ContainerView target) {
        super(target);
    }

    public void setCaption(String caption) {
        target.caption = caption;
    }

    public void setDescription(String description) {
        target.description = description;
    }

    public void setType(ContainerType type) {
        target.setType(type);
    }

    public void setChildrenAlignment(FlexAlignment falign) {
        Alignment align;
        switch (falign) {
            case LEADING:
                align = Alignment.LEADING;
                break;
            case CENTER:
                align = Alignment.CENTER;
                break;
            case TRAILING:
                align = Alignment.TRAILING;
                break;
            default:
                throw new IllegalStateException("Children alignment should be either of LEADING, CENTER, TRAILING");
        }
        target.setChildrenAlignment(align);
    }

    public void setColumns(int columns) {
        target.columns = columns;
    }

    public void setColumnLabelsWidth(int columnLabelsWidth) {
        target.columnLabelsWidth = columnLabelsWidth;
    }
}
