package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.TreeGroupView;

public class TreeGroupProxy extends ViewProxy<TreeGroupView> {
    public TreeGroupProxy(TreeGroupView target) {
        super(target);
    }

    public void setExpandOnClick(boolean expandOnClick) {
        target.expandOnClick = expandOnClick;
    }
}
