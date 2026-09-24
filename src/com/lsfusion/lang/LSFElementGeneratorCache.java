package com.lsfusion.lang;

import com.intellij.openapi.components.Service;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.UserDataHolderBase;

@Service(Service.Level.PROJECT)
public final class LSFElementGeneratorCache extends UserDataHolderBase {
    public static LSFElementGeneratorCache getInstance(Project project) {
        return project.getService(LSFElementGeneratorCache.class);
    }
}
