package com.lsfusion.design;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.debugger.engine.DebugProcessImpl;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.DataManager;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.actions.AggregateFormAction;
import com.lsfusion.actions.EnableLiveFormDesignEditing;
import com.lsfusion.debug.LSFDebuggerRunner;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.LSFLocalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFFormStatementImpl;
import lsfusion.server.physics.dev.debug.DebuggerService;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Level;
import org.jetbrains.annotations.NotNull;

import java.rmi.registry.LocateRegistry;

public class FormDesignChangeDetector extends PsiTreeChangeAdapter implements ProjectComponent {
    private final PsiDocumentManager psiDocumentManager;
    private final PsiManager psiManager;
    private final Project project;

    public FormDesignChangeDetector(final PsiDocumentManager psiDocumentManager, final PsiManager psiManager, final Project project) {
        this.psiDocumentManager = psiDocumentManager;
        this.psiManager = psiManager;
        this.project = project;
    }

    @Override
    public void projectOpened() {
        psiManager.addPsiTreeChangeListener(this);
    }

    @Override
    public void projectClosed() {
    }

    @Override
    public void initComponent() {
    }

    @Override
    public void disposeComponent() {
    }

    @NotNull
    @Override
    public String getComponentName() {
        return "FormDesignChangeDetector";
    }

    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
        fireChildChanged(event);
    }

    public static boolean alreadyPending; // do not synchronise, as everything is in edt

    private void fireChildChanged(PsiTreeChangeEvent event) {
        PsiElement element = event.getChild();
        PsiFile file = event.getFile();

        if (!alreadyPending) {
            alreadyPending = true;
            if (EnableLiveFormDesignEditing.isEnableFormDesignEditingEnabled(project))
                showLiveDesign(project, element, file);
            else
                showDefaultDesign(element, file);
        }
    }

    public static DebugProcessImpl debugProcess;
    private static String oldForm = null;
    public static void showLiveDesign(final Project project, PsiElement element, PsiFile file) {
        DumbService.getInstance(project).smartInvokeLater(() -> {
            if (debugProcess != null && element != null && file != null) { //until there is a client debugProcess will be null
                Document document = PsiDocumentManager.getInstance(project).getDocument(file);
                Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                if (selectedTextEditor != null && document != null) {
                    PsiElement elementAt = file.findElementAt(TargetElementUtil.adjustOffset(file, document, selectedTextEditor.getCaretModel().getOffset()));
                    if (elementAt != null && elementAt.getContainingFile() instanceof LSFFile) { //because in AggregateFormAction.getFormText is cast to the LSFFile
                        try {
                            Logger.getInstance(FormDesignChangeDetector.class).setLevel(Level.OFF); //turn off the logger because AggregateFormAction.getFormText periodically throws errors which are not caught
                            Integer userData = debugProcess.getUserData(LSFDebuggerRunner.DEBUGGER_PROPERTY_KEY);
                            LSFExtend parentOfType = PsiTreeUtil.getParentOfType(elementAt, LSFExtend.class);
                            if (userData != null && parentOfType != null) {
                                DebuggerService debuggerService = (DebuggerService) LocateRegistry.getRegistry("localhost", userData).lookup("lsfDebuggerService");
                                LSFId nameIdentifier = parentOfType.resolveDecl().getNameIdentifier();
                                if (debuggerService != null && nameIdentifier != null) {
                                    String currentForm = StringUtils.join(AggregateFormAction.getFormText(elementAt, true), "\n\n");
                                    if (oldForm == null || !oldForm.equals(currentForm)) {
                                        oldForm = currentForm;
                                        debuggerService.showFormDesign(currentForm, nameIdentifier.getName());
                                    }
                                }
                            }
                        } catch (Throwable ignored) {
                        } finally {
                            Logger.getInstance(FormDesignChangeDetector.class).setLevel(Level.ERROR); //turn on the logger
                            alreadyPending = false;
                        }
                    }
                }
            }
        });
    }

    private void showDefaultDesign(PsiElement element, PsiFile file) {
        if (element == null || file == null || !DesignViewFactory.getInstance().windowIsVisible() || psiDocumentManager.getDocument(file) == null || DumbService.isDumb(project))
            return;

        // as this event is called before commitTransaction, modificationStamp and unsavedDocument have not been updated, so the indexes cannot be accessed
        DumbService.getInstance(project).smartInvokeLater(() -> {
            try {
                Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
                LSFFormDeclaration formDeclaration = null;
                LSFModuleDeclaration module = null;
                LSFLocalSearchScope localScope = null;

                if (editor != null) {
                    PsiElement targetElement = ConfigurationContext.getFromContext(DataManager.getInstance().getDataContext(editor.getComponent())).getPsiLocation();
                    if (targetElement != null) {
                        LSFFormExtend formExtend = PsiTreeUtil.getParentOfType(targetElement, LSFFormExtend.class);
                        if (formExtend != null) {
                            localScope = LSFLocalSearchScope.createFrom(formExtend);
                            formDeclaration = ((LSFFormStatementImpl) formExtend).resolveFormDecl();
                        } else {
                            LSFDesignStatement designStatement = PsiTreeUtil.getParentOfType(targetElement, LSFDesignStatement.class);
                            if (designStatement != null) {
                                localScope = LSFLocalSearchScope.createFrom(designStatement);
                                formDeclaration = designStatement.resolveFormDecl();
                            }
                        }

                        if (targetElement.getContainingFile() instanceof LSFFile)
                            module = ((LSFFile) targetElement.getContainingFile()).getModuleDeclaration();
                    }
                }

                if (module != null && formDeclaration != null)
                    DesignViewFactory.getInstance().updateView(module, formDeclaration, localScope);

            } catch (PsiInvalidElementAccessException ignored) {
            } finally {
                alreadyPending = false;
            }
        });
    }
}
