package com.lsfusion;

import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.impl.BulkVirtualFileListenerAdapter;
import com.lsfusion.actions.locale.LSFPropertiesFileListener;
import com.lsfusion.mcp.LSFMcpRagFileListener;
import com.lsfusion.mcp.LocalMcpRagService;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFBaseStartupActivity implements ProjectActivity, DumbAware {
    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new LSFFileEditorManagerListener());
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkVirtualFileListenerAdapter(new LSFPropertiesFileListener(project)));
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkVirtualFileListenerAdapter(new LSFMcpRagFileListener(project)));
        try {
            LocalMcpRagService.getInstance(project).indexProjectAsync();
        } catch (Exception ignored) {
            // indexing service not available
        }
        return Unit.INSTANCE;
    }
}
