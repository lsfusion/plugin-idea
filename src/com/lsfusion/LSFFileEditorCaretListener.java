package com.lsfusion;

import com.intellij.codeInsight.folding.CodeFoldingManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.event.CaretAdapter;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.util.Key;
import com.lsfusion.lang.folding.LSFPropertyParamsFoldingManager;

import java.util.Timer;

public class LSFFileEditorCaretListener extends CaretAdapter {
    @Override
    public void caretPositionChanged(final CaretEvent e) {
        int newLine = e.getNewPosition().line;
        if (e.getOldPosition().line != newLine) {
            if (LSFPropertyParamsFoldingManager.rebuildFoldings(e.getEditor().getDocument(), newLine)) {
                new Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                ApplicationManager.getApplication().invokeLater(new Runnable() {
                                    @Override
                                    public void run() {
                                        e.getEditor().putUserData(Key.findKeyByName("code folding"), null);
                                        CodeFoldingManager.getInstance(e.getEditor().getProject()).updateFoldRegions(e.getEditor());
                                    }
                                });
                            }
                        },
                        1500
                );
            }
        }
    }
}
