package com.lsfusion.design.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ToolbarGridButton extends JButton {
    private static MouseAdapter mouseAdapter = new MouseAdapter() {
        @Override
        public void mouseEntered(MouseEvent e) {
            ((JButton) e.getSource()).setContentAreaFilled(true);
        }

        @Override
        public void mouseExited(MouseEvent e) {
            ((JButton) e.getSource()).setContentAreaFilled(false);
        }
    };

    public final static Dimension DEFAULT_SIZE = new Dimension(20, 20);

    public ToolbarGridButton(Icon icon) {
        this(icon, null);
    }
    
    public ToolbarGridButton(Icon icon, String toolTipText) {
        this(icon, toolTipText, DEFAULT_SIZE);

    }
    public ToolbarGridButton(Icon icon, String toolTipText, Dimension buttonSize) {
        super();
        setIcon(icon);
        setAlignmentY(Component.TOP_ALIGNMENT);
        setMinimumSize(buttonSize);
        setPreferredSize(buttonSize);
        setMaximumSize(buttonSize);
//        setFocusable(false);
        setBorder(null);
        if (toolTipText != null) {
            setToolTipText(toolTipText);
        }
        setContentAreaFilled(false);
        addMouseListener(mouseAdapter);
    }
}
