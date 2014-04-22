package com.lsfusion.design.model;

import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseEvent;

public class GridTable extends JBTable {
    public GridTable(TableModel model) {
        super(model);
        setColumnSelectionAllowed(false);

        getTableHeader().setReorderingAllowed(false);

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        refreshColumnModel();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        Dimension preferred = super.getPreferredScrollableViewportSize();
        return new Dimension(preferred.width, 50);
    }

    @Override
    protected JTableHeader createDefaultTableHeader() {
        return new GridTableHeader(columnModel);
    }

    private void refreshColumnModel() {
        int rowHeight = 0;
        for (int i = 0; i < getModel().getColumnCount(); ++i) {
            PropertyDrawView cell = getColumnProperty(i);

            TableColumn column = getColumnModel().getColumn(i);

            column.setMinWidth(cell.getMinimumWidth(this));
            column.setPreferredWidth(((getAutoResizeMode() == JTable.AUTO_RESIZE_OFF) ? cell.getMinimumWidth(this) : cell.getPreferredWidth(this)));
            column.setMaxWidth(cell.getMaximumWidth(this));

            column.setHeaderValue(getModel().getColumnName(i));

            rowHeight = Math.max(rowHeight, cell.getPreferredHeight(this));
        }

        if (getModel().getColumnCount() != 0) {
            if (getRowHeight() != rowHeight) {
                setRowHeight(rowHeight);
            }
        }
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

    public void setOrResetPreferredColumnWidths() {
        if (getAutoResizeMode() == JTable.AUTO_RESIZE_OFF) {
            setPreferredColumnWidthsAsMinWidth();
        } else {
            resetPreferredColumnWidths();
        }
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return fitWidth();
    }

    @Override
    public GridTableModel getModel() {
        return (GridTableModel) super.getModel();
    }

    private void setPreferredColumnWidthsAsMinWidth() {
        for (int i = 0; i < getModel().getColumnCount(); ++i) {
            getColumnModel().getColumn(i).setPreferredWidth(getColumnModel().getColumn(i).getMinWidth());
        }
    }

    private void resetPreferredColumnWidths() {
        for (int i = 0; i < getModel().getColumnCount(); ++i) {
            PropertyDrawView cell = getColumnProperty(i);
            getColumnModel().getColumn(i).setPreferredWidth(cell.getPreferredWidth(this));
        }
    }

    private PropertyDrawView getColumnProperty(int column) {
        return getModel().getColumnProperty(column);
    }

    private class GridTableHeader extends JTableHeader {
        public GridTableHeader(TableColumnModel columnModel) {
            super(columnModel);
        }

        @Override
        public String getToolTipText(MouseEvent e) {
            int index = columnModel.getColumnIndexAtX(e.getPoint().x);
            if (index == -1) {
                return super.getToolTipText(e);
            }
            int modelIndex = columnModel.getColumn(index).getModelIndex();

            return getColumnProperty(modelIndex).getTooltipText((String) columnModel.getColumn(index).getHeaderValue());
        }

        @Override
        public Dimension getPreferredSize() {
            return new Dimension(columnModel.getTotalColumnWidth(), 34);
        }
    }
}
