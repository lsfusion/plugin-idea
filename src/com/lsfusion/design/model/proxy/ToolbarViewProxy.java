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

    public void setShowCountQuantity(boolean showCountQuantity) {
        target.showCountRows = showCountQuantity;
    }

    public void setShowCalculateSum(boolean showCalculateSum) {
        target.showCalculateSum = showCalculateSum;
    }

    public void setShowGroup(boolean showGroup) {
        target.showGroupReport = showGroup;
    }

    public void setShowPrintGroupXls(boolean showPrintGroupXls) {
        target.showXls = showPrintGroupXls;
    }

    public void setShowSettings(boolean showSettings) {
        target.showSettings = showSettings;
    }
}
