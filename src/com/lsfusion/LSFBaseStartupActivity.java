package com.lsfusion;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.EditorFactory;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.ProjectActivity;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.intellij.openapi.vfs.impl.BulkVirtualFileListenerAdapter;
import com.lsfusion.actions.locale.LSFPropertiesFileListener;
import kotlin.Unit;
import kotlin.coroutines.Continuation;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFBaseStartupActivity implements ProjectActivity, DumbAware {
    @Override
    public @Nullable Object execute(@NotNull Project project, @NotNull Continuation<? super Unit> continuation) {
        // Everything here must be parented to the plugin's own disposable. A connection opened with the
        // no-argument connect(), or a caret listener left on an editor, is owned by the project and survives
        // a plugin unload, pinning the plugin's class loader so that updates can only be applied by restart.
        Disposable pluginScope = project.getService(LSFProjectDisposable.class);

        project.getMessageBus().connect(pluginScope)
                .subscribe(VirtualFileManager.VFS_CHANGES, new BulkVirtualFileListenerAdapter(new LSFPropertiesFileListener()));

        // The editor multicaster also covers editors restored before this activity runs, which a
        // fileOpened() subscription would miss.
        EditorFactory.getInstance().getEventMulticaster()
                .addCaretListener(new LSFFileEditorCaretListener(project), pluginScope);

        return Unit.INSTANCE;
    }
}
