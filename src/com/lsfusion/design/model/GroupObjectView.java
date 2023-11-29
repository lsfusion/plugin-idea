package com.lsfusion.design.model;

import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.ObjectEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.util.BaseUtils;

import java.util.ArrayList;

public class GroupObjectView extends ArrayList<ObjectView> implements PropertyGroupContainerView {
    public GroupObjectEntity entity;

    public GridView grid;
    public ToolbarView toolbarSystem;
    public ContainerView filtersContainer;
    public ComponentView filterControls;

    public GroupObjectView(GroupObjectEntity entity) {
        this.entity = entity;

        for (ObjectEntity obj : entity.objects) {
            add(new ObjectView(obj));
        }

        grid = new GridView(this);
        toolbarSystem = new ToolbarView(false);

        filtersContainer = new ContainerView();
        filtersContainer.horizontal = true;
        filtersContainer.setAlignment(FlexAlignment.STRETCH);
        filtersContainer.setCaption("Filters");
        
        filterControls = new FilterControlsView();
        filterControls.setAlignment(FlexAlignment.STRETCH);
    }

    public String getCaption() {
        return get(0).getCaption();
    }

    @Override
    public String getSID() {
        return entity.sID;
    }

    public void addGridPropertyDraw(PropertyDrawView property) {
        grid.addPropertyDraw(property);
        toolbarSystem.visible = entity.initClassView == ClassViewType.GRID;
    }

    public boolean isLastGroupInTree() {
        return entity.isInTree() && BaseUtils.last(entity.treeGroup.groups) == entity;
    }

    @Override
    public String getPropertyGroupContainerSID() {
        return getSID();
    }

    @Override
    public ContainerView getFiltersContainer() {
        return filtersContainer;
    }

    @Override
    public ComponentView getFilterControlsComponent() {
        return filterControls;
    }

    public void addFilter(FilterView filter) {
        filtersContainer.add(filter);
    }
}
