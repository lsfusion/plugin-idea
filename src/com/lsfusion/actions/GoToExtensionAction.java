package com.lsfusion.actions;

import com.intellij.codeInsight.CodeInsightActionHandler;
import com.intellij.codeInsight.actions.BaseCodeInsightAction;
import org.jetbrains.annotations.NotNull;

public class GoToExtensionAction extends BaseCodeInsightAction {
    @NotNull
    @Override
    protected CodeInsightActionHandler getHandler() {
        return new GoToExtensionHandler();
    }
}
