package com.lsfusion.design.model;

import javax.swing.table.AbstractTableModel;
import java.util.ArrayList;
import java.util.List;

public class GridTableModel extends AbstractTableModel {
    private List<PropertyDrawView> properties = new ArrayList<>();

    @Override
    public int getColumnCount() {
        return properties.size();
    }

    @Override
    public int getRowCount() {
        return 15;
    }

    @Override
    public String getColumnName(int column) {
        return properties.get(column).getCaption();
    }

    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        return null;
    }

    public void addPropertyDraw(PropertyDrawView property) {
        properties.add(property);
    }

    public PropertyDrawView getColumnProperty(int column) {
        return properties.get(column);
    }
}
