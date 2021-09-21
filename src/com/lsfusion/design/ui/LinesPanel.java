package com.lsfusion.design.ui;

import com.lsfusion.design.model.ContainerView;
import com.lsfusion.design.model.entity.FormEntity;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public class LinesPanel extends FlexPanel {

    public LinesPanel(ContainerView container, FormEntity formEntity, List<Component> children) {
        super(false, Alignment.START);

        int linesCount = container.lines;
        JPanel[] lines = new JPanel[linesCount];
        double lineFlex = getLineFlex(formEntity, container);
        for (int i = 0; i < linesCount; ++i) {
            JPanel line = new JPanel();
            line.setLayout(new FlexLayout(line, true, Alignment.START));
            line.setBorder(BorderFactory.createEmptyBorder(0,0,0,4));
            super.addImpl(line, new FlexConstraints(FlexAlignment.START, lineFlex), -1);

            lines[i] = line;
        }
        
        if (linesCount > 0) {
            for (int i = 0; i < children.size(); ++i) {
                Component child = children.get(i);
                if (child != null) {
                    int colIndex = i % linesCount;
                    lines[colIndex].add(child, new FlexConstraints(container.getChildren().get(i).getAlignment(), 0));
                }
            }
        }
    }

    private double getLineFlex(FormEntity formEntity, ContainerView container) {
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
