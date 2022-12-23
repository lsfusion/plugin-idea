package com.lsfusion.design.ui;

public enum ClassViewType {
    PANEL, TOOLBAR, GRID, HIDE;

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

}
