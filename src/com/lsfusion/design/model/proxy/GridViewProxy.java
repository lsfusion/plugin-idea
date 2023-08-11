package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.GridView;

@SuppressWarnings("unused")
public class GridViewProxy extends ComponentViewProxy<GridView> {
    public GridViewProxy(GridView target) {
        super(target);
    }

    public void setAutoSize(boolean autoSize) {
        target.autoSize = autoSize;
    }

    public void setBoxed(boolean boxed) {
        target.boxed = boxed;
    }

    public void setTabVertical(boolean tabVertical) {
        target.tabVertical = tabVertical;
    }

    public void setQuickSearch(boolean quickSearch) {
        target.quickSearch = quickSearch;
    }

    public void setHeaderHeight(int headerHeight) {
        target.headerHeight = headerHeight;
    }

    public void setResizeOverflow(boolean resizeOverflow) {
        target.resizeOverflow = resizeOverflow;
    }

    public void setLineWidth(int lineWidth) {
        target.lineWidth = lineWidth;
    }

    public void setLineHeight(int lineHeight) {
        target.lineHeight = lineHeight;
    }
}
