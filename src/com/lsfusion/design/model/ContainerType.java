package com.lsfusion.design.model;

public enum ContainerType {
    CONTAINERH,
    CONTAINERV,
    COLUMNS,
    TABBED,
    SCROLL,
    SPLITV,
    SPLITH;

    public boolean isTabbed() {
        return this == TABBED;
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

    public boolean isScroll() { return this == SCROLL; }
}
