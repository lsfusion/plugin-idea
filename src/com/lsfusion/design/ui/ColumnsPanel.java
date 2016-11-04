package com.lsfusion.design.ui;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ColumnsPanel extends FlexPanel {
    private final int columnsCount;
    private final JPanel[] columns;

    public ColumnsPanel(int columnCount, List<Component> children) {
        super(false, Alignment.LEADING);
        this.columnsCount = columnCount;

        columns = new JPanel[columnsCount];
        for (int i = 0; i < columnsCount; ++i) {
            JPanel column = new JPanel();
            column.setLayout(new ColumnsLayout(column, 1));
            super.addImpl(column, new FlexConstraints(), -1);

            columns[i] = column;
        }
        
        if (columnCount > 0) {
            for (int i = 0; i < children.size(); ++i) {
                Component child = children.get(i);
                if (child != null) {
                    int colIndex = i % columnCount;
                    columns[colIndex].add(child, new ColumnsConstraints(FlexAlignment.STRETCH));
                }
            }
        }
    }

    @Override
    protected void addImpl(Component comp, Object constraints, int index) {
        throw new IllegalStateException("Shouldn't be used directly");
    }

    @Override
    public void remove(int index) {
        throw new IllegalStateException("Shouldn't be used directly");
    }
}
