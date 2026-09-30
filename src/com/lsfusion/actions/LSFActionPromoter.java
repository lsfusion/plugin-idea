package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.ActionPromoter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LSFActionPromoter implements ActionPromoter {
    // UsagesSearchAction should be invoked before ShowUsagesAction and FindUsagesAction, and the insert composition
    // actions before Kotlin's Run Scratch File, which takes Ctrl+Alt+W in the default keymap too. The insert composition
    // actions are enabled in lsFusion files only, so in a Kotlin scratch the shortcut still runs the scratch
    private static boolean isPromoted(AnAction action) {
        return action instanceof UsagesSearchAction || action instanceof InsertCompositionAction;
    }

    private static final Comparator<AnAction> ACTIONS_COMPARATOR = (o1, o2) -> Boolean.compare(isPromoted(o2), isPromoted(o1));

    @Override
    public List<AnAction> promote(List<? extends AnAction> actions, DataContext context) {
        ArrayList<AnAction> result = new ArrayList<>(actions);
        result.sort(ACTIONS_COMPARATOR);
        return result;
    }
}
