package com.lsfusion.design;

import com.intellij.codeInsight.TargetElementUtil;
import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.DataManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;
import com.intellij.psi.*;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.actions.AggregateFormAction;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFLocalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFFormStatementImpl;
import com.lsfusion.util.Pair;
import lsfusion.server.physics.dev.debug.DebuggerService;
import org.apache.commons.lang.StringUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.rmi.RemoteException;

import static com.lsfusion.debug.DebugUtils.*;

public class FormDesignChangeDetector extends PsiTreeChangeAdapter implements ProjectManagerListener {
    private Project project;

    @Override
    public void projectOpened(@NotNull Project project) {
        this.project = project;
        PsiManager.getInstance(project).addPsiTreeChangeListener(this, () -> {});
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
            if (DesignView.isLiveFormDesignEditingEnable(project))
                showLiveDesign(project, element, file);
            else
                showDefaultDesign(element, file);
        }
    }

    private static Pair<String, String> getFormWithName(final Project project, @NotNull PsiFile file) {
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

    private static void evalClient(DebuggerService debuggerService, String formName, String currentForm, Project project) throws RemoteException {
        if (oldForm == null || !oldForm.equals(currentForm) || !DesignView.isLiveFormDesignEditingEnable(project)) {
            oldForm = currentForm;
            String currentFormIndexName = "debug_" + System.currentTimeMillis();

            debuggerService.evalClient(
                    "run(STRING form) { \n" +
                    "   showError() <- NULL;\n" +
                    "   TRY {\n" +
                    "       EVAL form + \'run() \\{ SHOW \\'" + currentFormIndexName + "\\' = " + formName + " NOWAIT; \\}\';\n" +
                    "       IF openedFormId() THEN { \n" +
                    "           EVAL \'run() \\{ CLOSE FORM \\'\' + openedFormId() + \'\\';\\}\';\n" +
                    "       }\n" +
                    "       APPLY {\n" +
                    "           openedFormId() <- '" + currentFormIndexName + "';\n" +
                    "       }\n" +
                    "   } CATCH { \n" +
                    "       showError() <- TRUE;\n" +
                    "   }\n" +
                    "}", currentForm + "\n" +
                    "EXTEND FORM " + formName + "\n" +
                    "   PROPERTIES() messageCaughtException SHOWIF showError(); \n" +
                    "DESIGN "+ formName + "{\n" +
                    "    MOVE PROPERTY(messageCaughtException()) FIRST {\n" +
                    "        background = RGB(245,0,0);\n" +
                    "        caption = '';\n" +
                    "        height = 70;\n" +
                    "        valueAlignment = CENTER;\n" +
                    "    }\n" +
                    "}");
        }
    }

    private static String oldForm = null;
    private static Runnable showForm;
    private static final Timer timer = new Timer(1000, e -> {if (showForm != null) showForm.run();}) {
        @Override
        public boolean isRepeats() {
            return false;
        }
    };

    public static void showLiveDesign(final Project project, PsiElement element, PsiFile file) {
        if (timer.isRunning())
            timer.stop();

        showForm = () -> {
            if (debugProcess != null && element != null && file != null) { //until there is a client debugProcess will be null
                DumbService.getInstance(project).smartInvokeLater(() -> {
                    try {
                        DebuggerService debuggerService = getDebuggerService();
                        Pair<String, String> formWithName = getFormWithName(project, file);
                        if (debuggerService != null && formWithName != null) {
                            evalClient(debuggerService, formWithName.first, formWithName.second, project);
                        }

                    } catch (Throwable ignored) {
                    } finally {
                        alreadyPending = false;
                    }
                });
            }
        };

        timer.start();
    }

    private void showDefaultDesign(PsiElement element, PsiFile file) {
        if (element == null || file == null || !DesignViewFactory.getInstance().windowIsVisible() || PsiDocumentManager.getInstance(project).getDocument(file) == null || DumbService.isDumb(project))
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
