package com.simpleplugin;

import com.intellij.codeInsight.lookup.LookupEx;
import com.intellij.codeInsight.lookup.LookupManager;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentSynchronizationVetoer;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.impl.source.tree.injected.InjectedLanguageUtil;
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
