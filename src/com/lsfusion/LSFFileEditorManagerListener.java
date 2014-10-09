package com.lsfusion;

import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.ex.EditorEx;
import com.intellij.openapi.fileEditor.FileEditor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerAdapter;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class LSFFileEditorManagerListener extends FileEditorManagerAdapter {
    private final LSFFileEditorCaretListener CARET_LISTENER = new LSFFileEditorCaretListener(); 
    
    @Override
    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        for (FileEditor fileEditor : source.getAllEditors(file)) {
            if (!(fileEditor instanceof TextEditor)) {
                continue;
            }

            final EditorEx editor = (EditorEx)((TextEditor)fileEditor).getEditor();
            CaretModel caretModel = editor.getCaretModel();
            
            caretModel.removeCaretListener(CARET_LISTENER);
            caretModel.addCaretListener(CARET_LISTENER);
        }
    }
}
