package com.simpleplugin.actions;

import com.intellij.codeInsight.daemon.DaemonBundle;
import com.intellij.codeInsight.daemon.impl.PsiElementListNavigator;
import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.lang.LanguageCodeInsightActionHandler;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.psi.NavigatablePsiElement;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.containers.ContainerUtil;
import com.simpleplugin.LSFLanguage;
import com.simpleplugin.psi.MetaChangeDetector;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LSFGoToSuperHandler implements LanguageCodeInsightActionHandler {
    @Override
    public boolean isValidFor(Editor editor, PsiFile file) {
        return file.getLanguage() == LSFLanguage.INSTANCE;
    }

    @Override
    public void invoke(@NotNull Project project, @NotNull Editor editor, @NotNull PsiFile file) {
        final PsiElement at = file.findElementAt(editor.getCaretModel().getOffset());
        if(at!=null) {
            LeafPsiElement mapped = (LeafPsiElement)MetaChangeDetector.mapOffset(at);
            if(mapped!=null) {
                mapped.navigate(true);
            }
        }
    }

    @Override
    public boolean startInWriteAction() {
        return true;
    }
}
