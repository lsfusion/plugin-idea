package com.lsfusion.design.model;

import java.util.LinkedHashSet;
import java.util.Set;

public class AbstractGroup {
    public String caption;
    public boolean createContainer = true;
    public String sID;


    public AbstractGroup parent;
    public Set<AbstractGroup> children = new LinkedHashSet<AbstractGroup>();

    public AbstractGroup(String sID, String caption) {
        this.sID = sID;
        this.caption = caption;
    }

    public void add(AbstractGroup child) {
        children.add(child);
    }
}
