package com.lsfusion.structure;

import com.lsfusion.LSFIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ExtendedClassesGroup extends PropertyStatementGroup {
    private String className;
    private String namespace;

    public ExtendedClassesGroup(String className, String namespace) {
        this.className = className;
        this.namespace = namespace;
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return className;
    }

    @Nullable
    @Override
    public Icon getIcon(boolean unused) {
        return LSFIcons.CLASS;
    }

    @Nullable
    @Override
    public String getLocationString() {
        return namespace;
    }
}
