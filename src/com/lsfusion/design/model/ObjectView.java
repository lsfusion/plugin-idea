package com.lsfusion.design.model;

import com.lsfusion.design.model.entity.ObjectEntity;

public class ObjectView {
    public ObjectEntity entity;

    public ObjectView(ObjectEntity entity) {
        this.entity = entity;
    }

    public String getCaption() {
        return entity.getCaption();
    }

    public String getSID() {
        return entity.sID;
    }
}
