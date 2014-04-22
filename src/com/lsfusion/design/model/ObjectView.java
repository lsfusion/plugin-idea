package com.lsfusion.design.model;

import com.lsfusion.design.model.entity.ObjectEntity;

public class ObjectView {
    public ObjectEntity entity;

    private GroupObjectView groupObject;

    public ClassChooserView classChooser;

    public ObjectView(ObjectEntity entity, GroupObjectView groupObject) {
        this.entity = entity;
        this.groupObject = groupObject;

        classChooser = new ClassChooserView(this);
    }

    public String getCaption() {
        return entity.getCaption();
    }

    public String getSID() {
        return entity.sID;
    }
}
