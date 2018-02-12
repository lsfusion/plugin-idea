package com.lsfusion.design.ui;

import java.awt.*;

public class FlexPanel extends JComponentPanel {
    private static final FlexConstraints DEFAULT_CONSTRAINTS = new FlexConstraints();
    
    private final boolean vertical;

    public FlexPanel(boolean vertical) {
        this(vertical, Alignment.START);
    }
    
    public FlexPanel(boolean vertical, Alignment childrenAlignment) {
        setLayout(null);
        this.vertical = vertical;

        setLayout(new FlexLayout(this, vertical, childrenAlignment));
    }

    public void add(Component child, double flex) {
        add(child, flex, FlexAlignment.START);
    }

    public void add(Component child, FlexAlignment align) {
        add(child, 0, align);
    }

    public void add(Component child, double flex, FlexAlignment align) {
        add(child, new FlexConstraints(align, flex));
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        if (constraints == null) {
            constraints = DEFAULT_CONSTRAINTS;
        }
        super.addImpl(comp, constraints, index);
    }
}
