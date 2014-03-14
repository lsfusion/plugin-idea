package com.lsfusion.design.model;

public enum ContainerType {
    CONTAINERH,
    CONTAINERV,
    COLUMNS,
    TABBED_PANE,
    VERTICAL_SPLIT_PANE,
    HORIZONTAL_SPLIT_PANE;

    public boolean isTabbed() {
        return this == TABBED_PANE;
    }

    public boolean isSplit() {
        return this == HORIZONTAL_SPLIT_PANE || this == VERTICAL_SPLIT_PANE;
    }

    public boolean isVerticalSplit() {
        return this == VERTICAL_SPLIT_PANE;
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
