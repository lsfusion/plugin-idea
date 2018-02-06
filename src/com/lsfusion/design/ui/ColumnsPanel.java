package com.lsfusion.design.ui;

import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.entity.FormEntity;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class ColumnsPanel extends FlexPanel {

    public ColumnsPanel(ContainerView container, FormEntity formEntity, List<Component> children) {
        super(false, Alignment.LEADING);

        int columnsCount = container.columns;
        JPanel[] columns = new JPanel[columnsCount];
        double columnFlex = getColumnFlex(formEntity, container);
        for (int i = 0; i < columnsCount; ++i) {
            JPanel column = new JPanel();
            column.setLayout(new FlexLayout(column, true, Alignment.LEADING));
            column.setBorder(BorderFactory.createEmptyBorder(0,0,0,4));
            super.addImpl(column, new FlexConstraints(FlexAlignment.LEADING, columnFlex), -1);

            columns[i] = column;
        }
        
        if (columnsCount > 0) {
            for (int i = 0; i < children.size(); ++i) {
                Component child = children.get(i);
                if (child != null) {
                    int colIndex = i % columnsCount;
                    columns[colIndex].add(child, new FlexConstraints(container.getChildren().get(i).getAlignment(), 0));
                }
            }
        }
    }

    private double getColumnFlex(FormEntity formEntity, ContainerView container) {
        ContainerView container2 = container.container;
        if (container2 == null || !container2.isHorizontal()) {
            return container.getAlignment() == FlexAlignment.STRETCH ? 1 : 0;
        }
        return container.getFlex(formEntity);
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
