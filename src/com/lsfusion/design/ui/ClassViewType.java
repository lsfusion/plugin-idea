package com.lsfusion.design.ui;

import java.util.ArrayList;
import java.util.List;

public enum ClassViewType {
    PANEL, TOOLBAR, GRID, HIDE;

    public static List<String> typeNameList() {
        List list = new ArrayList();
        for (int i = 0; i < ClassViewType.values().length; i++) {
            list.add(ClassViewType.values()[i].toString());
        }
        return list;
    }
    
    public boolean isPanel() {
        return this == PANEL || this == TOOLBAR;
    }

    public boolean isGrid() {
        return this == GRID;
    }
    
    public boolean isToolbar() {
        return this == TOOLBAR;
    }
    
    public boolean isHidden() {
        return this == HIDE;
    }

    public static ClassViewType switchView(ClassViewType initClassView) {
        if (initClassView == GRID)
            return PANEL;
        else
            return GRID;
    }
}
