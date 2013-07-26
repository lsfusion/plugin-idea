
package com.simpleplugin.psi;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.simpleplugin.LSFFileType;
import com.simpleplugin.LSFLanguage;
import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LSFFile extends PsiFileBase {
    public LSFFile(@NotNull FileViewProvider viewProvider) {
        super(viewProvider, LSFLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public FileType getFileType() {
        return LSFFileType.INSTANCE;
    }

    public LSFModuleDeclaration getModuleDeclaration() {
        return findChildByClass(LSFModuleHeader.class);
    }

    @Override
    public String toString() {
        return "Lsf File";
    }

    @Override
    public Icon getIcon(int flags) {
        return super.getIcon(flags);
    }
}