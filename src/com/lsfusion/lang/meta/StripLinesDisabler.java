package com.lsfusion.lang.meta;

import com.intellij.lang.Language;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.ex.DocumentEx;
import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.fileEditor.FileDocumentManager;
import com.intellij.openapi.fileEditor.FileDocumentManagerAdapter;
import com.intellij.openapi.fileTypes.ContentBasedFileSubstitutor;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.PlainTextLanguage;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.*;
import com.intellij.psi.impl.file.impl.FileManager;
import com.intellij.testFramework.LightVirtualFile;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.LSFLanguage;
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
