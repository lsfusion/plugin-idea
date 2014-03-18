package com.lsfusion.design.model;

public enum ContainerType {
    CONTAINERH,
    CONTAINERV,
    COLUMNS,
    TABBED_PANE,
    SPLITV,
    SPLITH;

    public boolean isTabbed() {
        return this == TABBED_PANE;
    }

    public boolean isSplit() {
        return this == SPLITH || this == SPLITV;
    }

    public boolean isVerticalSplit() {
        return this == SPLITV;
    }

    public boolean isVertical() {
        return this == CONTAINERV;
    }

    public boolean isHorizontal() {
        return this == CONTAINERH;
    }

    public boolean isLinear() {
        return isVertical() || isHorizontal();
    }

    public boolean isColumns() {
        return this == COLUMNS;
    }
}
