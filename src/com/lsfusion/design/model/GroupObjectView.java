package com.lsfusion.design.model;

import com.lsfusion.design.model.entity.GroupObjectEntity;
import com.lsfusion.design.model.entity.ObjectEntity;
import com.lsfusion.design.ui.ClassViewType;
import com.lsfusion.util.BaseUtils;

import java.util.ArrayList;

public class GroupObjectView extends ArrayList<ObjectView> implements PropertyGroupContainerView {
    public GroupObjectEntity entity;

    public GridView grid;
    public ShowTypeView showType;
    public ToolbarView toolbar;
    public FilterView filter = new FilterView();

    public Boolean needVerticalScroll = true;

    public GroupObjectView(GroupObjectEntity entity) {
        this.entity = entity;

        for (ObjectEntity obj : entity.objects) {
            add(new ObjectView(obj, this));
        }

        grid = new GridView(this);
        toolbar = new ToolbarView(false);
        showType = new ShowTypeView(this);
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
        toolbar.visible = entity.initClassView == ClassViewType.GRID;
    }

    public boolean isLastGroupInTree() {
        return entity.isInTree() && BaseUtils.last(entity.treeGroup.groups) == entity;
    }

    @Override
    public String getPropertyGroupContainerSID() {
        return getSID();
    }
}
