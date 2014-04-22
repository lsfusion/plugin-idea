package com.lsfusion.design.model.entity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RegularFilterGroupEntity {
    public String sID;

    public List<RegularFilterEntity> filters = new ArrayList<RegularFilterEntity>();

    public RegularFilterGroupEntity(String sID) {
        this.sID = sID;
    }

    public void addFilter(RegularFilterEntity filter) {
        filters.add(filter);
    }

    public GroupObjectEntity getToDraw(FormEntity form) {
        Set<ObjectEntity> groupObjects = new HashSet<ObjectEntity>();

        for (RegularFilterEntity regFilter : filters) {
            groupObjects.addAll(regFilter.objects);
        }

        return form.getApplyObject(groupObjects);
    }
}
