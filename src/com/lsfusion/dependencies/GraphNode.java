package com.lsfusion.dependencies;

public interface GraphNode {
    String getSID();
    
    boolean isDependent();
    
    void navigate();
}
