package com.lsfusion.design.ui;

import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class SingleCellTable extends JBTable {
    public SingleCellTable() {
        super(new DefaultTableModel(1, 1) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        });
        setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component comp = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (!hasFocus) {
                    setBackground(Color.white);
                }
                return comp;
            }
        });
        getColumnModel().setColumnMargin(2);
        setRowMargin(2);
        setBorder(BorderFactory.createLineBorder(Color.gray));
    }
}
