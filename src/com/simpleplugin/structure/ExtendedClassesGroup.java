package com.simpleplugin.structure;

import com.simpleplugin.LSFIcons;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class ExtendedClassesGroup extends PropertyStatementGroup {
    private String className;

    public ExtendedClassesGroup(String className) {
        this.className = className;
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
}
