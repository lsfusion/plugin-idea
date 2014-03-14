package com.lsfusion.design.ui;

import javax.swing.*;
import java.awt.*;

public class FlexPanel extends JPanel {
    private final boolean vertical;

    public FlexPanel(boolean vertical) {
        this(vertical, Alignment.LEADING);
    }
    
    public FlexPanel(boolean vertical, Alignment childrenAlignment) {
        super(null);
        this.vertical = vertical;

        setLayout(new FlexLayout(this, vertical, childrenAlignment));
    }
    
    public void add(Component child, double flex) {
        add(child, flex, FlexAlignment.LEADING);
    }

    public void add(Component child, FlexAlignment align) {
        add(child, 0, align);
    }

    public void add(Component child, double flex, FlexAlignment align) {
        add(child, new FlexConstraints(align, flex));
    }
}
