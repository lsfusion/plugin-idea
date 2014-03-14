package com.lsfusion.design.vfs;

import com.intellij.testFramework.LightVirtualFile;
import com.lsfusion.design.DesignInfo;
import org.jetbrains.annotations.NotNull;

public class LSFDesignVirtualFileImpl extends LightVirtualFile {
    private final DesignInfo designInfo;

    public LSFDesignVirtualFileImpl(@NotNull DesignInfo designInfo) {
        super(designInfo.getFormSID(), LSFDesignFileType.INSTANCE, "");
        setWritable(false);

        this.designInfo = designInfo;
    }

    @NotNull
    public DesignInfo getDesignInfo() {
        return designInfo;
    }
    
    public static LSFDesignVirtualFileImpl create(DesignInfo designInfo) {
        LSFDesignVirtualFileImpl file = new LSFDesignVirtualFileImpl(designInfo);
        file.setOriginalFile(designInfo.formDecl.getLSFFile().getVirtualFile());
        return file;
    }
}
