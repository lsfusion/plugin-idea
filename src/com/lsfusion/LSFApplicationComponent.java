package com.lsfusion;

import com.intellij.openapi.components.ApplicationComponent;
import org.apache.commons.lang.SystemUtils;
import org.jetbrains.annotations.NotNull;

public class LSFApplicationComponent implements ApplicationComponent {
    @Override
    public void initComponent() {
        // попытка исправить падающий иногда IllegalArgumentException, связанный с TimSort. исправлено в Java9
        // http://bugs.java.com/bugdatabase/view_bug.do?bug_id=7075600
        if (SystemUtils.JAVA_VERSION_FLOAT < 1.9) {
            System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        }
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "LSFApplicationComponent";
    }
}
