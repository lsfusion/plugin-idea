package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.ui.Alignment;
import com.lsfusion.design.ui.FlexAlignment;

@SuppressWarnings("unused")
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
    
    public void setCollapsible(boolean collapsible) {
        target.setCollapsible(collapsible);
    }

    public void setCollapsed(boolean collapsed) {
        target.setCollapsed(collapsed);
    }

    public void setType(ContainerType type) {
        target.setType(type);
    }

    public void setResizeOverflow(boolean resizeOverflow) {
        target.resizeOverflow = resizeOverflow;
    }

    public void setAlignCaptions(boolean alignCaptions) {
        target.alignCaptions = alignCaptions;
    }

    public void setChildrenAlignment(FlexAlignment falign) {
        Alignment align;
        switch (falign) {
            case START:
                align = Alignment.START;
                break;
            case CENTER:
                align = Alignment.CENTER;
                break;
            case END:
                align = Alignment.END;
                break;
            default:
                throw new IllegalStateException("Children alignment should be either of START, CENTER, END");
        }
        target.setChildrenAlignment(align);
    }

    //backward compatibility
    public void setColumns(int columns) {
        target.lines = columns;
    }

    public void setLines(int lines) {
        target.lines = lines;
    }

    public void setHorizontal(boolean horizontal) {
        target.horizontal = horizontal;
    }

    public void setTabbed(boolean tabbed) {
        target.tabbed = tabbed;
    }

    public void setShowIf(String showIf) {
        target.showIf = showIf;
    }
}
