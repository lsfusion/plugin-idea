package com.lsfusion.design.model.entity;

import java.util.Set;

import static com.lsfusion.util.BaseUtils.nvl;

public class RegularFilterEntity {
    public String name;
    public Set<ObjectEntity> objects;
    public boolean isDefault;

    public RegularFilterEntity(String name, Set<ObjectEntity> objects, boolean isDefault) {
        this.name = name;
        this.objects = objects;
        this.isDefault = isDefault;
    }

    public String getFullCaption() {
        //key/mouse, binding now not supported, if needed, do the same as in GRegularFilter
        return nvl(name, "");
    }
}
