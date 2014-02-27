package com.lsfusion.lang;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.lsfusion.LSFIcons;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LSFFileType extends LanguageFileType {
    public static final LSFFileType INSTANCE = new LSFFileType();

    private LSFFileType() {
        super(LSFLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "lsFusion script file";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "lsFusion language file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "lsf";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return LSFIcons.FILE;
    }
}