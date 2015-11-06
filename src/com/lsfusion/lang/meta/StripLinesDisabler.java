package com.lsfusion.lang.meta;

import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.vfs.VirtualFile;
import com.lsfusion.lang.LSFFileType;
import org.jetbrains.annotations.NotNull;

// необходимо убрать strip lines для тела метакода, чтобы не было рекурсивных вызовов, плюс есть проблема с guardPsiModificationsIn в doSaveDocumentInWriteAction и вызовом runTransaction внутри (впрочем ее возможно скоро пофиксят)
public class StripLinesDisabler extends FileDocumentManagerAdapter {

    @Override
    public void beforeDocumentSaving(@NotNull Document document) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if(file != null && file.getFileType() instanceof LSFFileType && document instanceof DocumentEx) {
            ((DocumentEx)document).setStripTrailingSpacesEnabled(false);
        }
    }
}
