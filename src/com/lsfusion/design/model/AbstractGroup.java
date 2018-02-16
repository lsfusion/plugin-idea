package com.lsfusion.design.model;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AbstractGroup {
    private static final List<String> NO_CONTAINER_GROUPS = Arrays.asList("root", "public", "private", "base", "recognize", "drilldown", "propertyPolicy"); 
    
    public String caption;
    public boolean createContainer = true;
    public String sID;


    public AbstractGroup parent;
    public Set<AbstractGroup> children = new LinkedHashSet<>();

    public AbstractGroup(String sID, String caption) {
        this.sID = sID;
        this.caption = caption;
        if (NO_CONTAINER_GROUPS.contains(sID)) {
            createContainer = false;
        }
    }

    public void add(AbstractGroup child) {
        children.add(child);
    }
}
