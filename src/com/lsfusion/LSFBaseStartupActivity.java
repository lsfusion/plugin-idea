package com.lsfusion;

import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.impl.BulkVirtualFileListenerAdapter;
import com.lsfusion.actions.locale.LSFPropertiesFileListener;
import org.jetbrains.annotations.NotNull;

public class LSFBaseStartupActivity implements StartupActivity, DumbAware {
    @Override
    public void runActivity(@NotNull Project project) {
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new LSFFileEditorManagerListener());
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkVirtualFileListenerAdapter(new LSFPropertiesFileListener(project)));
    }
}
