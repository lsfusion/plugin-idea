package com.lsfusion.design.ui;

import com.intellij.designer.model.ErrorInfo;
import com.intellij.designer.model.PropertiesContainer;
import com.intellij.designer.propertyTable.PropertyTable;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.util.ThrowableRunnable;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;

public class ComponentPropertyTable extends PropertyTable {
    @Override
    protected boolean doRestoreDefault(ThrowableRunnable<Exception> runnable) {
        return false;
    }

    @Override
    protected List<ErrorInfo> getErrors(@NotNull PropertiesContainer container) {
        return Collections.emptyList();
    }

    @Override
    protected boolean doSetValue(ThrowableRunnable<Exception> runnable) {
        return false;
    }

    @NotNull
    @Override
    protected TextAttributesKey getErrorAttributes(@NotNull HighlightSeverity severity) {
        return null;
    }
}
