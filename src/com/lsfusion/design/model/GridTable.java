package com.lsfusion.design.model;

import com.intellij.ui.table.JBTable;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTableUI;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import java.awt.*;
import java.awt.event.MouseEvent;

public class GridTable extends JBTable {
    public static final int DEFAULT_HEADER_HEIGHT = 34;
    public static Dimension DEFAULT_PREFERRED_SIZE = new Dimension(130, 130 - DEFAULT_HEADER_HEIGHT);

    private final boolean autoSize;
    private final int headerHeight;

    public GridTable(boolean autoSize, TableModel model, int headerHeight) {
        super(model);
        this.autoSize = autoSize;
        this.headerHeight = headerHeight;
        setColumnSelectionAllowed(false);
        
        setUI(new BasicTableUI());

        getTableHeader().setReorderingAllowed(false);

        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

        refreshColumnModel();
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return autoSize ? getPreferredSize() : DEFAULT_PREFERRED_SIZE;
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

            column.setMinWidth(cell.getValueWidth(this));
            column.setPreferredWidth(cell.getValueWidth(this));

            column.setHeaderValue(getModel().getColumnName(i));

            rowHeight = Math.max(rowHeight, cell.getValueHeight(this));
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
            getColumnModel().getColumn(i).setPreferredWidth(cell.getValueWidth(this));
        }
    }

    private PropertyDrawView getColumnProperty(int column) {
        return getModel().getColumnProperty(column);
    }
    
    private int getHeaderHeight() {
        return headerHeight <= 0 ? DEFAULT_HEADER_HEIGHT : headerHeight;
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
            return new Dimension(columnModel.getTotalColumnWidth(), getHeaderHeight());
        }
    }
}
