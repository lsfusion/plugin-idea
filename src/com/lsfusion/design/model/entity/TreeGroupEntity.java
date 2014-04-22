package com.lsfusion.design.model.entity;

import java.util.ArrayList;
import java.util.List;

public class TreeGroupEntity {
    public String sID;
    public List<GroupObjectEntity> groups = new ArrayList<GroupObjectEntity>();

    public TreeGroupEntity(String sID) {
        this.sID = sID;
    }

    public void addGroupObject(GroupObjectEntity groupObject) {
        groups.add(groupObject);
        groupObject.treeGroup = this;
    }
}
