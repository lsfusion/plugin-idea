package com.lsfusion.structure;

import com.lsfusion.LSFIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ModuleGroup extends PropertyStatementGroup {
    private String moduleName;

    public ModuleGroup(String moduleName) {
        this.moduleName = moduleName;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return moduleName;
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return LSFIcons.MODULE;
    }
}
