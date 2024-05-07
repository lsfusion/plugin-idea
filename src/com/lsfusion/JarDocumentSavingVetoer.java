package com.lsfusion;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.impl.ApplicationImpl;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentSynchronizationVetoer;
import com.intellij.openapi.fileEditor.impl.FileDocumentManagerImpl;
import com.intellij.openapi.vfs.VirtualFile;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.util.ReflectionUtils;
import org.jetbrains.annotations.NotNull;

public class JarDocumentSavingVetoer extends FileDocumentSynchronizationVetoer {
    @Override
    public boolean maySaveDocument(@NotNull Document document, boolean isSaveExplicit) {
        VirtualFile file = FileDocumentManager.getInstance().getFile(document);
        if (file != null && file.getFileType() == LSFFileType.INSTANCE && !document.isWritable()) {
            if (isClosing()) { // удаляем из unsaved, чтобы можно было закрыть проект
                FileDocumentManager.getInstance().reloadFromDisk(document);
            }
            return false;
        }
        
        return super.maySaveDocument(document, isSaveExplicit);
    }

    private boolean isClosing() {
        return ApplicationManager.getApplication().isDisposed() || myOnClose() || myExitInProgress();
    }
    
    // FileDocumentManagerImpl.myOnClose value
    private boolean myOnClose() {
        try {
            FileDocumentManagerImpl manager = (FileDocumentManagerImpl) FileDocumentManager.getInstance();
            return (boolean) ReflectionUtils.getPrivateFieldValue(manager, "myOnClose");
        } catch (Exception ignored) {
            return false;
        }
    }
    
    // ApplicationImpl.myExitInProgress value
    private boolean myExitInProgress() {
        try {
            ApplicationImpl app = (ApplicationImpl) ApplicationManager.getApplication();
            return (boolean) ReflectionUtils.getPrivateFieldValue(app, "myExitInProgress");
        } catch (Exception ignored) {
            return false;
        }
    }
}
