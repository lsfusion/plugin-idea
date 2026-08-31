package com.lsfusion;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.util.concurrency.AppExecutorUtil;
import com.lsfusion.lang.folding.LSFPropertyParamsFoldingManager;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

public class LSFFileEditorCaretListener implements CaretListener {
    private static final int UPDATE_FOLDINGS_DELAY = 1500;

    private final Project project;

    private final AtomicReference<ScheduledFuture<?>> pendingUpdate = new AtomicReference<>();

    public LSFFileEditorCaretListener(Project project) {
        this.project = project;
    }

    @Override
    public void caretPositionChanged(final CaretEvent e) {
        // Subscribed to the application-wide multicaster, so filter out the other projects' editors.
        if (e.getEditor().getProject() != project) {
            return;
        }

        int newLine = e.getNewPosition().line;

        if (e.getOldPosition().line != newLine) {
            if (LSFPropertyParamsFoldingManager.rebuildFoldings(e.getEditor().getDocument(), newLine)) {
                Editor editor = e.getEditor();
                // The shared scheduler instead of a java.util.Timer per caret move: each Timer spawned a
                // non-daemon thread that outlived its task and retained the event (and the plugin's class
                // loader) until the Timer itself was collected.
                ScheduledFuture<?> previous = pendingUpdate.getAndSet(AppExecutorUtil.getAppScheduledExecutorService()
                        .schedule(() -> ApplicationManager.getApplication().invokeLater(() -> {
                            if (editor.isDisposed())
                                return;
                            editor.putUserData(Key.findKeyByName("code folding"), null); // reset cache
                            LSFPropertyParamsFoldingManager.updateFoldRegions(editor);
                        }), UPDATE_FOLDINGS_DELAY, TimeUnit.MILLISECONDS));
                if (previous != null)
                    previous.cancel(false);
            }
        }
    }
}
