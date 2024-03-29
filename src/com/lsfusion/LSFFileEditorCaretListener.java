package com.lsfusion;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.lsfusion.lang.folding.LSFPropertyParamsFoldingManager;

import java.util.Timer;
import java.util.TimerTask;

public class LSFFileEditorCaretListener implements CaretListener {
    private CaretListenerTimerTask latestTask;

    @Override
    public void caretPositionChanged(final CaretEvent e) {
        int newLine = e.getNewPosition().line;

        if (e.getOldPosition().line != newLine) {
            if (LSFPropertyParamsFoldingManager.rebuildFoldings(e.getEditor().getDocument(), newLine)) {
                if (latestTask != null) {
                    latestTask.expired = true;
                }
                
                latestTask = new CaretListenerTimerTask(e);
                new Timer().schedule(
                        latestTask,
                        1500
                );
            }
        }
    }

    private class CaretListenerTimerTask extends TimerTask {
        boolean expired = false;
        private CaretEvent e;

        public CaretListenerTimerTask(final CaretEvent e) {
            this.e = e;
        }

        @Override
        public void run() {
            ApplicationManager.getApplication().invokeLater(() -> {
                Editor editor = e.getEditor();
                editor.putUserData(Key.findKeyByName("code folding"), null); // reset cache
                LSFPropertyParamsFoldingManager.updateFoldRegions(editor);
            }, (Condition) o -> expired);
        }
    }
}
