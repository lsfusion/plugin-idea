package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.FilterView;

public class FilterViewProxy extends ComponentViewProxy<FilterView> {
    public FilterViewProxy(FilterView target) {
        super(target);
    }

    public void setVisible(boolean visible) {
        target.visible = visible;
    }
}
