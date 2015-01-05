package com.lsfusion;

import com.intellij.codeInsight.folding.CodeFoldingManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretAdapter;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.Key;
import com.lsfusion.lang.folding.LSFPropertyParamsFoldingManager;

import java.util.Timer;
import java.util.TimerTask;

public class LSFFileEditorCaretListener extends CaretAdapter {
    private CaretListenerTimerTask latestTask;

    @Override
    public void caretPositionChanged(final CaretEvent e) {
        if (latestTask != null) {
            latestTask.expired = true;
        }

        int newLine = e.getNewPosition().line;

        if (e.getOldPosition().line != newLine) {
            if (LSFPropertyParamsFoldingManager.rebuildFoldings(e.getEditor().getDocument(), newLine)) {

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
            ApplicationManager.getApplication().invokeLater(new Runnable() {
                @Override
                public void run() {
                    Editor editor = e.getEditor();
                    editor.putUserData(Key.findKeyByName("code folding"), null); // reset cache
                    CodeFoldingManager.getInstance(editor.getProject()).updateFoldRegions(editor);

                    LSFPropertyParamsFoldingManager.foldingsRebuilt(editor.getDocument());
                }
            }, new Condition() {
                @Override
                public boolean value(Object o) {
                    return expired;
                }
            });
        }
    }
}
