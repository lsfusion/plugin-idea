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

    private Dimension componentSize;

    public void setComponentSize(Dimension componentSize) {
        this.componentSize = componentSize;
    }

    @Override
    public Dimension getPreferredSize() {
        return overrideSize(super.getPreferredSize(), componentSize);
    }

}
