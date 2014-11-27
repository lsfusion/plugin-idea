package com.lsfusion.actions;

import com.intellij.find.actions.ShowUsagesAction;
import com.intellij.openapi.actionSystem.AnAction;

public class PreShowPropertyUsagesAction extends SearchForPropertyUsagesAction {
    @Override
    protected AnAction getPlatformAction() {
        return new ShowUsagesAction();
    }
}
