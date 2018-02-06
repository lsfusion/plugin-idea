package com.lsfusion.design.model;

import com.intellij.ui.treeStructure.treetable.TreeTable;
import com.intellij.ui.treeStructure.treetable.TreeTableModel;
import org.jdesktop.swingx.JXTableHeader;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseEvent;

import static com.lsfusion.design.model.GridTable.DEFAULT_PREFERRED_SIZE;

public class TreeGroupTable extends TreeTable {
    private final int HIERARCHICAL_COLUMN_MIN_WIDTH = 50;
    private final int HIERARCHICAL_COLUMN_MAX_WIDTH = 100000;
    private TreeGroupView treeGroup;

    public TreeGroupTable(TreeGroupView treeGroup, TreeTableModel treeTableModel) {
        super(treeTableModel);
        this.treeGroup = treeGroup;
        setShowGrid(true);

        setColumnSelectionAllowed(false);

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        getTableHeader().setReorderingAllowed(false);
    }

    private PropertyDrawView getColumnProperty(int column) {
        return ((TreeGroupTableModel) getTableModel()).getColumnProperty(column);
    }

    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new JXTableHeader(columnModel) {
            @Override
            public Dimension getPreferredSize() {
                Dimension pref = super.getPreferredSize();
                return new Dimension(pref.width, 34);
            }

            @Override
            public String getToolTipText(MouseEvent e) {
                int index = columnModel.getColumnIndexAtX(e.getPoint().x);
                if (index == -1) {
                    return super.getToolTipText(e);
                }

                if (index == 0) {
                    return "Дерево";
                }

                PropertyDrawView property = getColumnProperty(index);
                return property.getTooltipText(property.getCaption());
            }
        };
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return (treeGroup != null && treeGroup.autoSize) ? getPreferredSize() : DEFAULT_PREFERRED_SIZE;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return fitWidth();
    }

    private boolean fitWidth() {
        int minWidth = 0;
        TableColumnModel columnModel = getColumnModel();

        for (int i = 0; i < getColumnCount(); i++) {
            if (autoResizeMode == JTable.AUTO_RESIZE_OFF) {
                minWidth += columnModel.getColumn(i).getWidth();
            } else {
                minWidth += columnModel.getColumn(i).getMinWidth();
            }
        }

        // тут надо смотреть pane, а не саму table
        return (minWidth < getParent().getWidth());
    }

    @Override
    public void doLayout() {
        int newAutoResizeMode = fitWidth()
                ? JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS
                : JTable.AUTO_RESIZE_OFF;
        if (newAutoResizeMode != autoResizeMode) {
            autoResizeMode = newAutoResizeMode;
            setAutoResizeMode(newAutoResizeMode);

            setOrResetPreferredColumnWidths();
        }
        super.doLayout();
    }

    public void setOrResetPreferredColumnWidths() {
        if (getAutoResizeMode() == JTable.AUTO_RESIZE_OFF) {
            setPreferredColumnWidthsAsMinWidth();
        } else {
            resetPreferredColumnWidths();
        }
    }

    private void setPreferredColumnWidthsAsMinWidth() {
        getColumnModel().getColumn(0).setPreferredWidth(HIERARCHICAL_COLUMN_MIN_WIDTH);
        for (int i = 1; i < getModel().getColumnCount(); ++i) {
            getColumnModel().getColumn(i).setPreferredWidth(getColumnModel().getColumn(i).getMinWidth());
        }
    }

    private void resetPreferredColumnWidths() {
        getColumnModel().getColumn(0).setPreferredWidth(treeGroup.calculateSize());
        for (int i = 1; i < getModel().getColumnCount(); ++i) {
            PropertyDrawView cell = getColumnProperty(i);
            getColumnModel().getColumn(i).setPreferredWidth(cell.getValueWidth(this));
        }
    }
}
