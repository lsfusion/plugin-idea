package com.lsfusion.actions.locale;

import com.intellij.lang.properties.PropertiesFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.*;
import com.lsfusion.lang.LSFResourceBundleUtils;
import org.jetbrains.annotations.NotNull;

public class LSFPropertiesFileListener implements VirtualFileListener {
    Project project;

    public LSFPropertiesFileListener(Project project) {
        this.project = project;
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        checkFile(event.getFile());
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        checkFile(event.getFile());
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        checkFile(event.getFile());
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        checkFile(event.getFile());
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        checkFile(event.getFile());
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        checkFile(event.getFile());
    }

    private void checkFile(VirtualFile file) {
        if (file.getFileType() instanceof PropertiesFileType) {
            LSFResourceBundleUtils.updateFile(project, file);
        }
    }
}