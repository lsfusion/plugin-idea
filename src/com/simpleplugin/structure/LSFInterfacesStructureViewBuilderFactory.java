package com.simpleplugin.structure;

import com.intellij.ide.structureView.StructureViewBuilder;
import com.intellij.ide.structureView.StructureViewBuilderProvider;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.simpleplugin.LSFFileType;
import com.simpleplugin.psi.LSFFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFInterfacesStructureViewBuilderFactory implements StructureViewBuilderProvider {
    @Nullable
    @Override
    public StructureViewBuilder getStructureViewBuilder(@NotNull FileType fileType, @NotNull final VirtualFile file, @NotNull Project project) {
        if (!(fileType instanceof LSFFileType)) return null;

        final PsiFile psiFile = PsiManager.getInstance(project).findFile(file);
        if (psiFile == null || !(psiFile instanceof LSFFile)) return null;

        LSFTreeBasedStructureViewBuilder.INSTANCE.setFile((LSFFile) psiFile);

        return LSFTreeBasedStructureViewBuilder.INSTANCE;
    }
}                                                       
