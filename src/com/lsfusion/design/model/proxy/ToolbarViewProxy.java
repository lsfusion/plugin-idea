package com.lsfusion.design.model.proxy;

import com.lsfusion.design.model.ToolbarView;

@SuppressWarnings("unused")
public class ToolbarViewProxy extends ComponentViewProxy<ToolbarView> {
    public ToolbarViewProxy(ToolbarView target) {
        super(target);
    }

    public void setVisible(boolean visible) {
        target.visible = visible;
    }

    public void setShowViews(boolean showViews) {
        target.showViews = showViews;
    }

    //deprecated since 6.0, will be removed in 7.0
    @Deprecated
    public void setShowGroup(boolean showGroup) {
        setShowViews(showGroup);
    }

    public void setShowFilters(boolean showFilters) {
        target.showFilters = showFilters;
    }

    public void setShowSettings(boolean showSettings) {
        target.showSettings = showSettings;
    }

    public void setShowCountQuantity(boolean showCountQuantity) {
        target.showCountQuantity = showCountQuantity;
    }

    public void setShowCalculateSum(boolean showCalculateSum) {
        target.showCalculateSum = showCalculateSum;
    }

    public void setShowPrintGroupXls(boolean showPrintGroupXls) {
        target.showPrintGroupXls = showPrintGroupXls;
    }

    public void setShowManualUpdate(boolean showManualUpdate) {
        target.showManualUpdate = showManualUpdate;
    }
}
