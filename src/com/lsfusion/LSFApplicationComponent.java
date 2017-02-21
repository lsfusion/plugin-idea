package com.lsfusion;

import com.intellij.openapi.components.ApplicationComponent;
import io.netty.util.internal.PlatformDependent;
import org.jetbrains.annotations.NotNull;

public class LSFApplicationComponent implements ApplicationComponent {
    @Override
    public void initComponent() {
        // попытка исправить падающий иногда IllegalArgumentException, связанный с TimSort. исправлено в Java9
        // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=7075600
        if (PlatformDependent.javaVersion() < 9) {
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        }
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "LSFApplicationComponent";
    }
}
