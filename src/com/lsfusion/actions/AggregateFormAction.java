package com.lsfusion.actions;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
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
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.util.Segment;
import com.intellij.psi.*;
import com.intellij.psi.search.searches.ReferencesSearch;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.components.JBScrollPane;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFReferenceAnnotator;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.references.LSFFullNameReference;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.DesignUtils;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregateFormAction extends AnAction {

    private Project project;

    @Override
    public void update(AnActionEvent e) {

        Object editorComponent = e.getDataContext().getData("contextComponent");
        if (editorComponent instanceof EditorComponentImpl) {
            EditorImpl editor = ((EditorComponentImpl) editorComponent).getEditor();
            if (PsiTreeUtil.getParentOfType(findSourceElement(e.getProject(), editor, editor.getCaretModel().getOffset()), LSFExtend.class) == null)
                e.getPresentation().setEnabled(false);
        }
    }

    List<String> codeBlocks;

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        this.project = getEventProject(e);
        final Progressive progress = indicator -> ApplicationManager.getApplication().runReadAction(() -> {
            codeBlocks = new ArrayList<>();

            Object editorComponent = e.getDataContext().getData("contextComponent");
            if (editorComponent instanceof EditorComponentImpl) {
                EditorImpl editor = ((EditorComponentImpl) editorComponent).getEditor();
                codeBlocks.addAll(getFormText(findSourceElement(e.getProject(), editor, editor.getCaretModel().getOffset()), false));
            }
        });

        ProgressManager.getInstance().run(new Task.Modal(e.getProject(), "Aggregating form", true) {
            public void run(final @NotNull ProgressIndicator indicator) {
                progress.run(indicator);
            }
        });

        if (!codeBlocks.isEmpty())
            new AggregateFormDialog(StringUtils.join(codeBlocks, "\n\n")).show();
    }

    public static List<String> getFormText(PsiElement sourceElement, boolean skipCreatingNotes) {
        List<LSFModuleDeclaration> requiredModules = DesignUtils.getRequiredModules(((LSFFile) sourceElement.getContainingFile()).getModuleDeclaration());
        List<String> result = new ArrayList<>();

        final LSFExtend lsfExtend = PsiTreeUtil.getParentOfType(sourceElement, LSFExtend.class);
        if (lsfExtend != null) {
            LSFFormDeclaration formDecl = (LSFFormDeclaration) lsfExtend.resolveDecl();
            LSFFile lsfFile = lsfExtend.getLSFFile();

            //FORM
            Map<PsiElement, LSFModuleDeclaration> elementToModule = new HashMap<>();
            LSFGlobalResolver.findExtendElements(formDecl, LSFStubElementTypes.EXTENDFORM, lsfFile, LSFLocalSearchScope.createFrom(lsfExtend))
                    .findAll().forEach(formExtend -> {
                        LSFModuleDeclaration moduleDeclaration = formExtend.getLSFFile().getModuleDeclaration();
                        if (moduleDeclaration != null)
                            elementToModule.put(formExtend, moduleDeclaration);
                    });

            //DESIGN
            LSFId formName = formDecl.getNameIdentifier();
            if (formName != null)
                ReferencesSearch.search(formName, lsfFile.getRequireScope()).findAll().stream().filter(ref -> ref instanceof LSFFormUsage)
                        .map(ref -> (LSFFormUsage) ref).filter(formUsage -> formUsage.getParent() instanceof LSFDesignHeader).forEach(formUsage -> {
                            LSFModuleDeclaration moduleDeclaration = formUsage.getLSFFile().getModuleDeclaration();
                            if (moduleDeclaration != null)
                                elementToModule.put(formUsage, moduleDeclaration);
                        });

            DesignUtils.sortByModules(requiredModules, elementToModule).forEach(formExtend -> result.add(formExtend.getParent() instanceof LSFDesignHeader ?
                    getCodeBlock(formExtend.getParent().getParent(), skipCreatingNotes, formDecl) : getCodeBlock(formExtend, skipCreatingNotes, formDecl) + "\n;"));
        }
        return result;
    }

    private class AggregateFormDialog extends DialogWrapper {

        private String text;

        public AggregateFormDialog(String text) {
            super(project);
            this.text = text;
            init();
            setTitle("Aggregate form");
        }

        @Override
        protected @Nullable JComponent createCenterPanel() {
            JTextPane sourceTextPane = new JTextPane();
            sourceTextPane.setText(text);
            sourceTextPane.setBackground(null);
            sourceTextPane.setEditable(false);
            return new JBScrollPane(sourceTextPane);
        }
    }

    private static String getCodeBlock(PsiElement element, boolean skipCreatingNotes, LSFFormDeclaration formDecl) {
        if (skipCreatingNotes)
            return getAggregatedText(element, formDecl);

        final PsiFile file = element.getContainingFile();
        final Document document = PsiDocumentManager.getInstance(element.getProject()).getDocument(file);
        final SmartPsiElementPointer<?> pointer = SmartPointerManager.getInstance(element.getProject()).createSmartPsiElementPointer(element);
        final Segment range = pointer.getRange();
        if (document != null && range != null) {
            int lineNumber = document.getLineNumber(range.getStartOffset()) + 1;
            int linePosition = range.getStartOffset() - document.getLineStartOffset(lineNumber - 1) + 1;
            return String.format("//%s (%s:%s)\n%s", file.getName(), lineNumber, linePosition, getAggregatedText(element, formDecl));
        }
        return null;
    }

    private static String getAggregatedText(PsiElement element, LSFFormDeclaration formDecl) {
        Project project = element.getProject();

        //read source elements offsets
        Map<Integer, String> offsetSourceMap = new HashMap<>();
        for (LSFFullNameReference child : PsiTreeUtil.findChildrenOfAnyType(element, LSFFullNameReference.class)) {
            LSFDeclaration declaration = child.resolveDecl();
            if (declaration != null) {
                boolean hasNamespace = child.getFullNameRef() != null;
                boolean needNamespace = !declaration.equals(formDecl);
                if (hasNamespace != needNamespace)
                    offsetSourceMap.put(child.getTextOffset() - element.getTextOffset(), needNamespace ? declaration.getLSFFile().getModuleDeclaration().getNamespace() + "." + child.getName() : child.getName());
            }
        }

        Map<PsiElement, LSFCompoundID> replacementMap = new HashMap<>();
        //copy element
        PsiElement copyElement = LSFElementGenerator.createFormFromText(project, element.getText(), element.getClass());

        //create elements with namespaces
        for (LSFFullNameReference sourceElement : PsiTreeUtil.findChildrenOfAnyType(copyElement, LSFFullNameReference.class)) {
            String replace = offsetSourceMap.get(sourceElement.getTextOffset() - copyElement.getTextOffset());
            if (replace != null)
                replacementMap.put(sourceElement, LSFElementGenerator.createCompoundIDFromText(project, replace));
        }

        //replace elements
        replacementMap.forEach(PsiElement::replace);

        if (copyElement instanceof LSFFormStatement) {

            //remove editFormDeclaration
            ((LSFFormStatement) copyElement).getEditFormDeclarationList()
                    .forEach(editForm -> editForm.replace(LSFElementGenerator.createPsiCommentFromText(project, editForm.getText() + " // Unable to call EDIT in interpreter")));

            //remove listFormDeclaration
            ((LSFFormStatement) copyElement).getListFormDeclarationList()
                    .forEach(listForm -> listForm.replace(LSFElementGenerator.createPsiCommentFromText(project, listForm.getText() + " // Unable to call LIST in interpreter")));

            //remove semicolon
            LSFEmptyStatement semicolon = ((LSFFormStatement) copyElement).getEmptyStatement();
            if (semicolon != null)
                semicolon.delete();
        }

        //replace new lines from the start of the text
        String text = copyElement.getText().replaceAll("^[\n\r]*", "");
        return LSFReferenceAnnotator.isInMetaDecl(element) ? "//this code is inside of meta declaration\n//" + text.replace("\n", "\n//") : text;
    }

    public PsiElement findSourceElement(Project project, Editor editor, int offset) {
        if (TargetElementUtil.inVirtualSpace(editor, offset)) return null;

        final Document document = editor.getDocument();
        PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(document);
        if (file == null) return null;

        if (file instanceof PsiCompiledElement) {
            PsiElement mirror = ((PsiCompiledElement) file).getMirror();
            if (mirror instanceof PsiFile) file = (PsiFile) mirror;
        }

        return file.findElementAt(TargetElementUtil.adjustOffset(file, document, offset));
    }

    @Override
    public @NotNull ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}