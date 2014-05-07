package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.model.entity.RegularFilterEntity;
import com.lsfusion.design.model.entity.RegularFilterGroupEntity;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegularFilterGroupView extends ComponentView {

    public RegularFilterGroupEntity entity;

    private List<RegularFilterView> filters = new ArrayList<RegularFilterView>();

    public RegularFilterGroupView(RegularFilterGroupEntity entity) {
        super(entity.sID);
        this.entity = entity;

        for (RegularFilterEntity filterEntity : entity.filters) {
            filters.add(new RegularFilterView(filterEntity));
        }
    }

    public RegularFilterView get(RegularFilterEntity filterEntity) {
        for (RegularFilterView filter : filters) {
            if (filter.entity == filterEntity) {
                return filter;
            }
        }
        return null;
    }

    @Override
    public String getCaption() {
        return "Regular filter";
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.FILTER_GROUP;
    }

    public boolean isSingle() {
        return filters.size() == 1;
    }

    @Override
    protected JComponent createWidgetImpl(Project project, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponent> componentToWidget, JComponent oldWidget) {
        if (isSingle()) {
            RegularFilterEntity filterEntity = filters.get(0).entity;
            return new JBCheckBox(filterEntity.getFullCaption(), filterEntity.isDefault);
        } else {
            List<Object> items = new ArrayList<Object>();
            Object defaultItem = "(Все)";
            items.add(defaultItem);
            for (RegularFilterView regFilter : filters) {
                String filterItem = regFilter.entity.getFullCaption();
                if (regFilter.entity.isDefault) {
                    defaultItem = filterItem;
                }
                items.add(filterItem);
            }
            ComboBox comboBox = new ComboBox(items.toArray(new Object[items.size()]), -1);
            comboBox.setSelectedItem(defaultItem);
            return comboBox;
        }
    }
}