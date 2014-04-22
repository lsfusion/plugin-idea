package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.GroupObjectView;

public class GroupObjectViewProxy extends ViewProxy<GroupObjectView> {
    public GroupObjectViewProxy(GroupObjectView target) {
        super(target);
    }

    public void setNeedVerticalScroll(Boolean needVerticalScroll) {
        target.needVerticalScroll = needVerticalScroll;
    }
}
