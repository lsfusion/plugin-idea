package com.lsfusion;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentSynchronizationVetoer;
import org.jetbrains.annotations.NotNull;

public class JarDocumentSavingVetoer extends FileDocumentSynchronizationVetoer {

    public boolean maySaveDocument(@NotNull Document document, boolean isSaveExplicit) {

        if(!document.isWritable()) {
            if(ApplicationManager.getApplication().isDisposeInProgress()) { // удаляем из unsaved, чтобы можно было закрыть проект
                FileDocumentManager.getInstance().reloadFromDisk(document);
            }
                    
            return false;
        }
        
        return super.maySaveDocument(document, isSaveExplicit);
    }

}
