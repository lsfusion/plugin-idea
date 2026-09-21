
package com.lsfusion.lang;

import com.intellij.lang.Language;
import org.jetbrains.annotations.NotNull;

public class LSFLanguage extends Language {
    public static final LSFLanguage INSTANCE = new LSFLanguage();

    private LSFLanguage() {
        super("Lsf");
    }

    // the ID "Lsf" stays: plugin.xml extensions and stored code style settings refer to it; this is what the IDE shows
    // wherever it lists languages (Code Style, Inspections, scratch files, language injection)
    @Override
    public @NotNull String getDisplayName() {
        return "lsFusion";
    }
}