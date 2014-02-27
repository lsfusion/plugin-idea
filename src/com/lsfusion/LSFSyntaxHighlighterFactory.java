package com.lsfusion;

import com.intellij.openapi.fileTypes.SyntaxHighlighterFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

public class LSFSyntaxHighlighterFactory extends SyntaxHighlighterFactory {
    @NotNull
    @Override
    public com.intellij.openapi.fileTypes.SyntaxHighlighter getSyntaxHighlighter(Project project, VirtualFile virtualFile) {
        return new LSFSyntaxHighlighter();
    }
}
