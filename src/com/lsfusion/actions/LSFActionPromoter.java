package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.ActionPromoter;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataContext;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LSFActionPromoter implements ActionPromoter {
    private static final Comparator<AnAction> ACTIONS_COMPARATOR = (o1, o2) -> {
        // UsagesSearchAction should be invoked before ShowUsagesAction and FindUsagesAction
        if (o1 instanceof UsagesSearchAction) {
            return -1;
        }
        if (o2 instanceof UsagesSearchAction) {
            return 1;
        }
        return 0;
    };

    @Override
    public List<AnAction> promote(List<? extends AnAction> actions, DataContext context) {
        ArrayList<AnAction> result = new ArrayList<>(actions);
        result.sort(ACTIONS_COMPARATOR);
        return result;
    }
}
