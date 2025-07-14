package com.lsfusion.lang.psi;

import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;

public interface LSFElement extends PsiElement {

    boolean isCorrect(); // для pin'ов, если pin сработал а не все элементы есть
    
    LSFFile getLSFFile();

    default boolean isInLibrarySources() {
        LSFFile lsfFile = getLSFFile();
        if (lsfFile != null) {
            VirtualFile virtualFile = lsfFile.getVirtualFile();
            if (virtualFile != null) {
                return ProjectFileIndex.getInstance(getProject()).isInLibrarySource(virtualFile);
            }
        }
        return false;
    }
}
