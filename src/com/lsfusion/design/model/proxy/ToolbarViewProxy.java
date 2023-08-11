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
        target.showCountQuantity = showCountQuantity;
    }

    public void setShowCalculateSum(boolean showCalculateSum) {
        target.showCalculateSum = showCalculateSum;
    }

    public void setShowGroup(boolean showGroup) {
        target.showGroup = showGroup;
    }

    public void setShowPrintGroupXls(boolean showPrintGroupXls) {
        target.showPrintGroupXls = showPrintGroupXls;
    }

    public void setShowSettings(boolean showSettings) {
        target.showSettings = showSettings;
    }
}
