package com.lsfusion;

import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;

public class LSFBaseProjectComponent implements ProjectComponent {
    private Project project = null;

    public LSFBaseProjectComponent(final Project project) {
        this.project = project;
    }
    
    @Override
    public void projectOpened() {
        project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new LSFFileEditorManagerListener()); 
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
