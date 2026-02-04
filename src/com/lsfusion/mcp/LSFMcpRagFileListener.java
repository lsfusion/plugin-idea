package com.lsfusion.mcp;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileCopyEvent;
import com.intellij.openapi.vfs.VirtualFileEvent;
import com.intellij.openapi.vfs.VirtualFileListener;
import com.intellij.openapi.vfs.VirtualFileMoveEvent;
import com.intellij.openapi.vfs.VirtualFilePropertyEvent;
import org.jetbrains.annotations.NotNull;

public class LSFMcpRagFileListener implements VirtualFileListener {
    private final Project project;

    public LSFMcpRagFileListener(Project project) {
        this.project = project;
    }

    @Override
    public void contentsChanged(@NotNull VirtualFileEvent event) {
        update(event.getFile());
    }

    @Override
    public void fileCreated(@NotNull VirtualFileEvent event) {
        update(event.getFile());
    }

    @Override
    public void fileDeleted(@NotNull VirtualFileEvent event) {
        delete(event.getFile());
    }

    @Override
    public void fileMoved(@NotNull VirtualFileMoveEvent event) {
        update(event.getFile());
    }

    @Override
    public void fileCopied(@NotNull VirtualFileCopyEvent event) {
        update(event.getFile());
    }

    @Override
    public void propertyChanged(@NotNull VirtualFilePropertyEvent event) {
        update(event.getFile());
    }

    private void update(VirtualFile file) {
        try {
            LocalMcpRagService.getInstance(project).updateFile(file);
        } catch (Exception ignored) {
            // service not available
        }
    }

    private void delete(VirtualFile file) {
        try {
            LocalMcpRagService.getInstance(project).deleteFile(file);
        } catch (Exception ignored) {
            // service not available
        }
    }
}
