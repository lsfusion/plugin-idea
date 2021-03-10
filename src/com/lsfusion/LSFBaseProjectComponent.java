package com.lsfusion;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.impl.BulkVirtualFileListenerAdapter;
import com.lsfusion.actions.locale.LSFPropertiesFileListener;
import org.jetbrains.annotations.NotNull;

public class LSFBaseProjectComponent implements ProjectComponent {
    private Project project = null;

    public LSFBaseProjectComponent(final Project project) {
        this.project = project;
    }
    
    @Override
    public void projectOpened() {
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new LSFFileEditorManagerListener());
        project.getMessageBus().connect().subscribe(VirtualFileManager.VFS_CHANGES, new BulkVirtualFileListenerAdapter(new LSFPropertiesFileListener(project)));
    }

    @Override
    public void projectClosed() {

    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "LSFBaseProjectComponent";
    }
}
