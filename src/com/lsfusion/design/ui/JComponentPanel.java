package com.lsfusion.design.ui;

import com.intellij.ui.components.JBPanel;

import javax.swing.*;
import java.awt.*;

import static com.lsfusion.util.BaseUtils.overrideSize;

public class JComponentPanel extends JBPanel {
    public JComponentPanel(JComponent component) {
        super(new BorderLayout());
        add(component, BorderLayout.CENTER);
    }

    public JComponentPanel(LayoutManager layout) {
        super(layout);
    }
    
    public JComponentPanel(boolean isDoubleBuffered) {
        super(isDoubleBuffered);
    }
    
    public JComponentPanel() {
    }

    private Dimension componentMinimumSize;
    private Dimension componentMaximumSize;
    private Dimension componentPreferredSize;

    public void setComponentMinimumSize(Dimension componentMinimumSize) {
        this.componentMinimumSize = componentMinimumSize;
    }

    public void setComponentMaximumSize(Dimension componentMaximumSize) {
        this.componentMaximumSize = componentMaximumSize;
    }

    public void setComponentPreferredSize(Dimension componentPreferredSize) {
        this.componentPreferredSize = componentPreferredSize;
    }

    @Override
    public Dimension getMinimumSize() {
        return overrideSize(super.getMinimumSize(), componentMinimumSize);
    }

    @Override
    public Dimension getMaximumSize() {
        return overrideSize(super.getMaximumSize(), componentMaximumSize);
    }

    @Override
    public Dimension getPreferredSize() {
        return overrideSize(super.getPreferredSize(), componentPreferredSize);
    }

}
