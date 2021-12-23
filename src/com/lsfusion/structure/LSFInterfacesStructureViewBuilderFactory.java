package com.lsfusion.structure;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewBuilderProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.psi.LSFFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFInterfacesStructureViewBuilderFactory implements StructureViewBuilderProvider {
    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder(@NotNull FileType fileType, @NotNull final VirtualFile file, @NotNull Project project) {
        if (!(fileType instanceof LSFFileType)) return null;

        final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (!(psiFile instanceof LSFFile)) return null;

        LSFStructureFileCaretListener caretListener = project.getUserData(LSFStructureFileCaretListener.PROJECT_COMPONENT_KEY);
        if (caretListener == null) {
            caretListener = new LSFStructureFileCaretListener(project);
            project.putUserData(LSFStructureFileCaretListener.PROJECT_COMPONENT_KEY, caretListener);
        }

        return new LSFTreeBasedStructureViewBuilder((LSFFile) psiFile, caretListener);
    }
}                                                       
