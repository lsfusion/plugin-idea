package com.lsfusion.design.ui;

import java.awt.*;

import static java.lang.Math.max;

public class ColumnsLayout extends CachableLayout<ColumnsConstraints> {

    //assert columns != 0
    int columns = 4;

    public ColumnsLayout(Container target, int columns) {
        super(target);
        if (columns <= 0) {
            throw new IllegalArgumentException("columns count shold be > 0");
        }

        this.columns = columns;
    }


    @Override
    protected ColumnsConstraints getDefaultContraints() {
        return new ColumnsConstraints();
    }

    @Override
    protected ColumnsConstraints cloneConstraints(ColumnsConstraints original) {
        return (ColumnsConstraints) original.clone();
    }

    @Override
    protected Dimension layoutSize(Container parent, ComponentSizeGetter sizeGetter) {
        int childCnt = target.getComponentCount();
        if (childCnt == 0) {
            return new Dimension(0, 0);
        }

        int visibleColumnsCnt = 0;

        int width = 0;
        int height = 0;
        for (int i = 0; i < columns; ++i) {
            int labelsWidth = 0;
            int contentWidth = 0;
            int contentHeight = 0;
            int columnWidth = 0;
            int rowsCnt = 0;
            for (int j = i; j < childCnt; j += columns) {
                Component child = target.getComponent(j);

                if (child.isVisible()) {
                    ++rowsCnt;

                    Dimension childSize = sizeGetter.get(child);

                    columnWidth = max(columnWidth, childSize.width);

                    contentHeight = limitedSum(contentHeight, childSize.height);
                }
            }

            if (rowsCnt > 0) {
                visibleColumnsCnt++;

                columnWidth = max(columnWidth, limitedSum(contentWidth, labelsWidth));

                width = limitedSum(width, columnWidth);
                height = max(height, contentHeight);
            }
        }

        return new Dimension(width, height);
    }

    @Override
    public void layoutContainer(Container parent) {
        checkParent(parent);

        Insets in = target.getInsets();
        Dimension size = target.getSize();

        int parentWidth = size.width - in.left - in.right;
        int parentHeight = size.height - in.top - in.bottom;

        int childCnt = target.getComponentCount();
        if (childCnt == 0) {
            return;
        }

        int currentX = in.left;
        for (int i = 0; i < columns; ++i) {
            int currentY = in.top;

            //1й проход для рассчёта ширины
            int labelsWidth = 0;
            int contentWidth = 0;
            int columnWidth = 0;
            boolean columnVisible = false;
            for (int j = i; j < childCnt; j += columns) {
                Component child = target.getComponent(j);

                if (child.isVisible()) {
                    int childWidth = child.getPreferredSize().width;
                    columnWidth = max(columnWidth, childWidth);
                    columnVisible = true;
                }
            }

            columnWidth = max(columnWidth, limitedSum(contentWidth, labelsWidth));

            if (columnVisible) {
                //2й проход проставляет размеры
                for (int j = i; j < childCnt; j += columns) {
                    Component child = target.getComponent(j);

                    if (child.isVisible()) {
                        Dimension prefSize = child.getPreferredSize();
                        FlexAlignment align = lookupConstraints(child).getAlignment();
                        int childWidth = align == FlexAlignment.STRETCH ? columnWidth : prefSize.width;
                        int childLeft = currentX;
                        switch (align) {
                            case CENTER: childLeft = (columnWidth - childWidth) / 2; break;
                            case TRAILING: childLeft = columnWidth - childWidth; break;
                            case LEADING:
                            case STRETCH:
                            default: break;
                        }

                        child.setBounds(childLeft, currentY, columnWidth, prefSize.height);

                        currentY += prefSize.height;
                    }
                }
            }
            currentX += columnWidth;
        }
    }
}
