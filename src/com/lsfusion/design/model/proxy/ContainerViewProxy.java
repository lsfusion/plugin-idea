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
        target.setCaption(caption);
    }

    public void setImage(String image) {
        target.image = image;
    }

    public void setCollapsible(boolean collapsible) {
        target.collapsible = collapsible;
    }

    public void setPopup(boolean popup) {
        target.popup = popup;
    }

    public void setBorder(boolean border) {
        target.border = border;
    }

    public void setCollapsed(boolean collapsed) {
        target.collapsed = collapsed;
    }

    //deprecated in 5.2, removed in 6.0
    public void setType(ContainerType type) {
        target.setType(type);
    }

    public void setHorizontal(boolean horizontal) {
        target.horizontal = horizontal;
    }

    public void setTabbed(boolean tabbed) {
        target.tabbed = tabbed;
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

    public void setAlignCaptions(boolean alignCaptions) {
        target.alignCaptions = alignCaptions;
    }

    public void setGrid(boolean grid) {
        target.grid = grid;
    }

    public void setWrap(boolean wrap) {
        target.wrap = wrap;
    }

    public void setResizeOverflow(boolean resizeOverflow) {
        target.resizeOverflow = resizeOverflow;
    }

    public void setCustom(String custom) {
        target.custom = custom;
    }

    //deprecated in 5.2, removed in 6.0
    public void setColumns(int columns) {
        setLines(columns);
    }

    public void setLines(int lines) {
        target.setLines(lines);
    }

    public void setLineSize(int lineSize) {
        target.lineSize = lineSize;
    }

    public void setCaptionLineSize(int captionLineSize) {
        target.captionLineSize = captionLineSize;
    }

    public void setVisible(boolean visible) {
        target.visible = visible;
    }
}
