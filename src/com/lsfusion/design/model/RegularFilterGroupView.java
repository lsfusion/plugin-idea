package com.lsfusion.design.model;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.ui.components.JBCheckBox;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.FormView;
import com.lsfusion.design.model.entity.FormEntity;
import com.lsfusion.design.model.entity.RegularFilterEntity;
import com.lsfusion.design.model.entity.RegularFilterGroupEntity;
import com.lsfusion.design.ui.JComponentPanel;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class RegularFilterGroupView extends ComponentView {

    private FormView form;
    public RegularFilterGroupEntity entity;

    private List<RegularFilterView> filters = new ArrayList<>();

    public RegularFilterGroupView(FormView form, RegularFilterGroupEntity entity) {
        super(entity.sID);
        this.form = form;
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
    protected JComponentPanel createWidgetImpl(Project project, FormEntity formEntity, Map<ComponentView, Boolean> selection, Map<ComponentView, JComponentPanel> componentToWidget, JComponentPanel oldWidget) {
        GroupObjectView groupObjectView = form.get(entity.getToDraw(form.entity));
        if (!groupObjectView.grid.isVisible()) {
            return null;
        }
        if (isSingle()) {
            RegularFilterEntity filterEntity = filters.get(0).entity;
            return new JComponentPanel(new JBCheckBox(filterEntity.getFullCaption(), filterEntity.isDefault));
        } else {
            List<Object> items = new ArrayList<>();
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
            return new JComponentPanel(comboBox);
        }
    }
}
