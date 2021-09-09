package com.lsfusion.actions;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.impl.EditorComponentImpl;
import com.intellij.openapi.editor.impl.EditorImpl;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Progressive;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Segment;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.components.JBScrollPane;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFormStatement;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class AggregateFormAction extends AnAction {

    @Override
    public void update(AnActionEvent e) {

        Object editorComponent = e.getDataContext().getData("contextComponent");
        if (editorComponent instanceof EditorComponentImpl) {

            EditorImpl editor = ((EditorComponentImpl) editorComponent).getEditor();
            PsiElement sourceElement = findSourceElement(e.getProject(), editor, editor.getCaretModel().getOffset());
            final LSFFormDeclaration declParent = PsiTreeUtil.getParentOfType(sourceElement, LSFFormDeclaration.class);

            if (declParent == null) {
                e.getPresentation().setEnabled(false);
            }
        }
    }

    List<String> codeBlocks;
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {

        final Progressive progress = indicator -> ApplicationManager.getApplication().runReadAction(() -> {
            codeBlocks = new ArrayList<>();
            List<String> result = new ArrayList<>();

            Object editorComponent = e.getDataContext().getData("contextComponent");
            if (editorComponent instanceof EditorComponentImpl) {

                EditorImpl editor = ((EditorComponentImpl) editorComponent).getEditor();
                PsiElement sourceElement = findSourceElement(e.getProject(), editor, editor.getCaretModel().getOffset());
                final LSFFormDeclaration declParent = PsiTreeUtil.getParentOfType(sourceElement, LSFFormDeclaration.class);

                if (declParent != null) {
                    Set<PsiFile> psiFiles = new LinkedHashSet<>();

                    ApplicationManager.getApplication().runReadAction(() -> {

                        result.add(getFormCodeBlock(sourceElement));
                        psiFiles.add(sourceElement.getContainingFile());
                        for (PsiElement element : declParent.processImplementationsSearch()) {
                            result.add(getFormCodeBlock(element));
                            psiFiles.add(element.getContainingFile());
                        }

                        for (PsiFile psiFile : psiFiles) {
                            for (LSFDesignStatement designDeclaration : PsiTreeUtil.findChildrenOfType(psiFile, LSFDesignStatement.class)) {
                                LSFFormDeclaration formDecl = designDeclaration.getDesignHeader().getFormUsage().resolveDecl();
                                if (declParent.equals(formDecl)) {
                                    result.add(getCodeBlock(designDeclaration));
                                }
                            }
                        }
                        codeBlocks.addAll(result);
                    });
                }
            }
        });

        Task task = new Task.Modal(e.getProject(), "Aggregating form", true) {
            public void run(final @NotNull ProgressIndicator indicator) {
                progress.run(indicator);
            }
        };

        ProgressManager.getInstance().run(task);

        if(!codeBlocks.isEmpty()) {
            new AggregateFormDialog(StringUtils.join(codeBlocks, "\n\n")).setVisible(true);
        }
    }

    private static class AggregateFormDialog extends JDialog {

        public AggregateFormDialog(String text) {
            super(null, "Aggregate form", ModalityType.APPLICATION_MODAL);
            setMinimumSize(new Dimension(600, 400));

            setLocationRelativeTo(null);

            JTextPane sourceTextPane = new JTextPane();
            sourceTextPane.setText(text);
            sourceTextPane.setBackground(null);
            sourceTextPane.setEditable(false);
            JBScrollPane sourceScrollPane = new JBScrollPane(sourceTextPane);

            JPanel buttonsPanel = new JPanel();
            JButton okButton = new JButton("OK");
            okButton.addActionListener(e -> dispose());
            buttonsPanel.add(okButton, BorderLayout.CENTER);

            add(sourceScrollPane, BorderLayout.CENTER);
            add(buttonsPanel, BorderLayout.SOUTH);
        }
    }

    private String getFormCodeBlock(PsiElement element) {
        LSFFormStatement formStatement = PsiTreeUtil.getParentOfType(element, LSFFormStatement.class);
        return formStatement != null ? getCodeBlock(formStatement) : null;
    }

    private String getCodeBlock(PsiElement element) {
        final PsiFile file = element.getContainingFile();
        final Document document = PsiDocumentManager.getInstance(element.getProject()).getDocument(file);
        final SmartPsiElementPointer<?> pointer = SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element);
        final Segment range = pointer.getRange();
        if (document != null && range != null) {
            int lineNumber = document.getLineNumber(range.getStartOffset()) + 1;
            int linePosition = range.getStartOffset() - document.getLineStartOffset(lineNumber - 1) + 1;
            return String.format("//%s (%s:%s);\n %s", file.getName(), lineNumber, linePosition, element.getText());
        }
        return null;
    }

    public PsiElement findSourceElement(Project project, Editor editor, int offset) {
        if (TargetElementUtil.inVirtualSpace(editor, offset)) {
            return null;
        }

        final Document document = editor.getDocument();
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file == null) return null;

        if (file instanceof PsiCompiledElement) {
            PsiElement mirror = ((PsiCompiledElement) file).getMirror();
            if (mirror instanceof PsiFile) file = (PsiFile) mirror;
        }

        return file.findElementAt(TargetElementUtil.adjustOffset(file, document, offset));
    }
}