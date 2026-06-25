package com.lsfusion.design.ui;

import com.intellij.designer.model.PropertiesContainer;
import com.intellij.designer.model.Property;
import com.intellij.ui.table.JBTable;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableCellRenderer;
import java.awt.Component;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Read-only property grid for the form design preview. Reimplements the small slice of the
 * platform-internal {@code com.intellij.designer.propertyTable.PropertyTable} that the design view relied on
 * (name/value rows, expert filtering, selection tracking) on top of the public {@link JBTable}.
 */
public class ComponentPropertyTable extends JBTable {
    private final PropertyTableModel model = new PropertyTableModel();
    private List<PropertiesContainer> containers = Collections.emptyList();
    private boolean showExpert = false;

    public ComponentPropertyTable() {
        setModel(model);
        getColumnModel().getColumn(1).setCellRenderer(new ValueCellRenderer());
    }

    public void update(@Nullable List<? extends PropertiesContainer> newContainers, @Nullable Property selection) {
        containers = newContainers == null ? Collections.emptyList() : new ArrayList<>(newContainers);
        rebuildAndKeepSelection(selection);
    }

    public void showExpert(boolean value) {
        if (showExpert != value) {
            showExpert = value;
            rebuildAndKeepSelection(getSelectionProperty());
        }
    }

    public boolean isShowExpertProperties() {
        return showExpert;
    }

    @Nullable
    public Property getSelectionProperty() {
        int row = getSelectedRow();
        return row < 0 ? null : model.getProperty(convertRowIndexToModel(row));
    }

    private void rebuildAndKeepSelection(@Nullable Property selection) {
        model.rebuild();
        if (selection != null) {
            int row = model.indexOf(selection);
            if (row >= 0) {
                getSelectionModel().setSelectionInterval(row, row);
            }
        }
    }

    private String getValueText(Property property) {
        String result = null;
        for (PropertiesContainer container : containers) {
            String text;
            try {
                Object value = property.getValue(container);
                text = value == null ? "" : value.toString();
            } catch (Exception e) {
                text = "";
            }
            if (result == null) {
                result = text;
            } else if (!result.equals(text)) {
                return ""; // differing values across a multi-selection
            }
        }
        return result == null ? "" : result;
    }

    private final class PropertyTableModel extends AbstractTableModel {
        private final List<Property> rows = new ArrayList<>();

        private void rebuild() {
            rows.clear();
            if (!containers.isEmpty()) {
                List<Property> properties = containers.get(0).getProperties();
                for (Property property : properties) {
                    if (!showExpert && property.isExpert()) {
                        continue;
                    }
                    if (property.availableFor(containers)) {
                        rows.add(property);
                    }
                }
            }
            fireTableDataChanged();
        }

        private Property getProperty(int row) {
            return rows.get(row);
        }

        private int indexOf(Property property) {
            return rows.indexOf(property);
        }

        @Override
        public int getRowCount() {
            return rows.size();
        }

        @Override
        public int getColumnCount() {
            return 2;
        }

        @Override
        public String getColumnName(int column) {
            return column == 0 ? "Property" : "Value";
        }

        @Override
        public boolean isCellEditable(int row, int column) {
            return false;
        }

        @Override
        public Object getValueAt(int row, int column) {
            Property property = rows.get(row);
            return column == 0 ? property.getName() : getValueText(property);
        }
    }

    private final class ValueCellRenderer implements TableCellRenderer {
        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Property property = model.getProperty(convertRowIndexToModel(row));
            PropertiesContainer container = containers.isEmpty() ? null : containers.get(0);
            JComponent component = property.getRenderer().getComponent(container, null, value, isSelected, hasFocus);
            component.setOpaque(true);
            component.setBackground(isSelected ? table.getSelectionBackground() : table.getBackground());
            component.setForeground(isSelected ? table.getSelectionForeground() : table.getForeground());
            return component;
        }
    }
}
