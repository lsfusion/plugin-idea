package com.lsfusion.design.model;

public class ObjectView {

    public String sID;

    private GroupObjectView groupObject;

    public ClassChooserView classChooser;

    public ObjectView() {
        this("");
    }

    public ObjectView(String sID) {
        this.sID = sID;
    }

    public ObjectView(GroupObjectView groupTo) {

        this.groupObject = groupTo;

        classChooser = new ClassChooserView(this);
    }

    public ObjectView(GroupObjectView groupTo, boolean clChooser) {

        this.groupObject = groupTo;

        classChooser = new ClassChooserView(this);
    }


    public String getCaption() {
        return "";
    }
}
