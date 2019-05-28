package com.lsfusion.lang.psi.declarations.policies;

import com.lsfusion.lang.classes.LSFClassSet;

import java.util.List;

public interface DBNamingPolicy {
    String createAutoTableDBName(List<LSFClassSet> classes);
    
    String transformActionOrPropertyCNToDBName(String canonicalName);
    
    String transformTableCNToDBName(String canonicalName);

    String getAutoTablesPrefix();
}