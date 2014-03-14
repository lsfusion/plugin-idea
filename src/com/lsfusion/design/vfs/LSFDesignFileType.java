package com.lsfusion.design.vfs;

import com.intellij.openapi.fileTypes.ex.FakeFileType;
import com.intellij.openapi.vfs.VirtualFile;
import com.lsfusion.LSFIcons;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LSFDesignFileType extends FakeFileType {

    public final static LSFDesignFileType INSTANCE = new LSFDesignFileType();

    @NotNull
    public String getName() {
        return "LSF Design";
    }

    @NotNull
    public String getDescription() {
        return getName() + " Fake File Type";
    }

    @Override
    public Icon getIcon() {
        return LSFIcons.Design.DESIGN;
    }

    public boolean isMyFileType(VirtualFile file) {
        return file instanceof LSFDesignVirtualFileImpl;
    }
}
