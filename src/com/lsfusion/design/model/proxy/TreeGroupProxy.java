package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.TreeGroupView;

@SuppressWarnings("unused")
public class TreeGroupProxy extends ViewProxy<TreeGroupView> {
    public TreeGroupProxy(TreeGroupView target) {
        super(target);
    }

    public void setAutoSize(boolean autoSize) {
        target.autoSize = autoSize;
    }

    public void setBoxed(boolean boxed) {
        target.boxed = boxed;
    }

    public void setExpandOnClick(boolean expandOnClick) {
        target.expandOnClick = expandOnClick;
    }

    public void setHierarchicalWidth(int hierarchicalWidth) {
        target.hierarchicalWidth = hierarchicalWidth;
    }

    public void setHierarchicalCaption(String hierarchicalCaption) {
        target.hierarchicalCaption = hierarchicalCaption;
    }

    public void setHeaderHeight(int headerHeight) {
        target.headerHeight = headerHeight;
    }

    public void setResizeOverflow(boolean resizeOverflow) {
        target.resizeOverflow = resizeOverflow;
    }

    public void setLineHeight(int lineHeight) {
        target.lineHeight = lineHeight;
    }

    public void setLineWidth(int lineWidth) {
        target.lineWidth = lineWidth;
    }
}
