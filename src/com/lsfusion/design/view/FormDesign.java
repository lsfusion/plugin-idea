package com.lsfusion.design.view;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.ui.update.MergingUpdateQueue;
import com.intellij.util.ui.update.Update;
import com.lsfusion.actions.AggregateFormAction;
import com.lsfusion.design.ui.FlexPanel;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.util.Pair;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.util.function.Consumer;

public abstract class FormDesign implements Disposable {
    protected final Project project;

    protected final ToolWindowEx toolWindow;

    protected FlexPanel mainPanel;

    protected String oldForm = null;

    protected final MergingUpdateQueue myRebuildQueue;

    public FormDesign(Project project, ToolWindowEx toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        myRebuildQueue = new MergingUpdateQueue("DesignView", 400, false, toolWindow.getComponent(), this, toolWindow.getComponent(), false);
        myRebuildQueue.setRestartTimerOnAdd(true);
    }

    protected Pair<String, String> getFormWithName(@NotNull PsiFile file) {
        Document document = PsiDocumentManager.getInstance(project).getDocument(file);
        Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (selectedTextEditor != null && document != null) {
            PsiElement element = file.findElementAt(TargetElementUtil.adjustOffset(file, document, selectedTextEditor.getCaretModel().getOffset()));
            LSFExtend parentOfType = PsiTreeUtil.getParentOfType(element, LSFExtend.class);
            if (parentOfType != null && element.getContainingFile() instanceof LSFFile)
                return new Pair<>(parentOfType.getGlobalName(), StringUtils.join(AggregateFormAction.getFormText(element, true), "\n\n"));
        }
        return null;
    }

    public void scheduleRebuild(String identity, PsiFile file, Consumer<Pair<String, String>> formConsumer) {
        myRebuildQueue.queue(new Update(identity) {
            @Override
            public void run() {
                new Task.Backgroundable(project, "Rebuilding form") {
                    @Override
                    public void run(@NotNull ProgressIndicator indicator) {
                        if (!project.isDisposed() && mainPanel.isShowing()) {
                            Pair<String, String> formWithName = DumbService.getInstance(project).runReadActionInSmartMode(() -> getFormWithName(file));
                            if (formWithName != null) {
                                String currentForm = formWithName.second;
                                if (oldForm == null || !oldForm.equals(currentForm)) {
                                    oldForm = currentForm;

                                    formConsumer.accept(formWithName);
                                }
                            }
                        }
                    }
                }.queue();
            }
        });
    }

    public JComponent getComponent() {
        return mainPanel;
    }

    @Override
    public void dispose() {
    }
}
