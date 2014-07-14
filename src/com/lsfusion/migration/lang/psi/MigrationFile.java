package com.lsfusion.migration.lang.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.lsfusion.migration.lang.MigrationFileType;
import com.lsfusion.migration.lang.MigrationLanguage;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class MigrationFile extends PsiFileBase {
    public MigrationFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, MigrationLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return MigrationFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Lsf migration script file";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}