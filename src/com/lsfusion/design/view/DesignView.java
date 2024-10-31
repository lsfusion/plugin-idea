package com.lsfusion.design.view;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiTreeChangeEvent;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.components.JBTabbedPane;
import com.intellij.util.Consumer;
import com.lsfusion.lang.psi.LSFDesignStatement;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFLocalSearchScope;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFFormStatementImpl;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class DesignView extends JBTabbedPane {
    private final Project project;
    private final ToolWindowEx toolWindow;
    private EmbeddedDesign embeddedDesign;
    private LiveDesign liveDesign;
    private ReportsPanel reportsPanel;
    
    private LSFFormDeclaration currentForm;
    private LSFModuleDeclaration currentModule;

    public DesignView(@NotNull Project project, final ToolWindowEx toolWindow) {
        this.project = project;
        this.toolWindow = toolWindow;

        liveDesign = new LiveDesign(project, toolWindow);
        addTab("Live", liveDesign.getComponent());
        
        embeddedDesign = new EmbeddedDesign(project, toolWindow);
        addTab("Embedded", embeddedDesign.getComponent());
        
        reportsPanel = new ReportsPanel(project);
        addTab("Jasper Reports", reportsPanel);

        ActionManager.getInstance().addTimerListener(new TimerListener() {
            @Override
            public ModalityState getModalityState() {
                return ModalityState.stateForComponent(toolWindow.getComponent());
            }

            @Override
            public void run() {
                if (toolWindow.isVisible()) {
                    maybeUpdateView();
                }
            }
        });

        addChangeListener(e -> {
            if (isEmbeddedDesignEnabled()) {
                embeddedDesign.onActivated();
            } else if (isReportsTabEnabled()) {
                reportsPanel.onActivated();
            }
        });
    }
    
    public void toolWindowInitialized() {
        if (isLiveDesignEnabled()) {
            liveDesign.onActivated();
        }
    }

    public void designCodeChanged(PsiTreeChangeEvent event) {
        PsiElement element = event.getChild();
        PsiFile file = event.getFile();

        if (isLiveDesignEnabled()) {
            if (!liveDesign.isManualMode()) {
                liveDesign.scheduleRebuild(element, file);
            }
        } else if (isEmbeddedDesignEnabled()) {
            embeddedDesign.scheduleRebuild(element, file);
        }
    }

    private void maybeUpdateView() {
        if (project.isDisposed()) return;

        final Component owner = KeyboardFocusManager.getCurrentKeyboardFocusManager().getFocusOwner();
        final boolean insideToolwindow = SwingUtilities.isDescendingFrom(toolWindow.getComponent(), owner);
        if (insideToolwindow || JBPopupFactory.getInstance().isPopupActive()) {
            return;
        }

        final DataContext dataContext = DataManager.getInstance().getDataContext(owner);
        if (CommonDataKeys.PROJECT.getData(dataContext) != project) return;

        new Task.Backgroundable(project, "Reading form") {
            PsiElement targetElement;
            TargetForm targetForm;
            VirtualFile[] files;
            PsiFile containingFile;

            @Override
            public void onSuccess() {
                try {
                    if (files != null && files.length == 1 && targetForm != null && targetForm.form != null) {
                        String formName = targetForm.form.getDeclName();
                        if (formName != null) {
                            // Slow operations are prohibited on EDT
                            if (containingFile instanceof LSFFile) {
                                targetForm.module = ((LSFFile) containingFile).getModuleDeclaration();
                            }
                            if (targetForm.module != null &&
                                    // additional check to avoid reading the aggregate form and comparing it later
                                    (currentForm != targetForm.form || currentModule != targetForm.module)) {
                                formUnderCaretChanged(targetForm);
                            }
                        }
                    }
                } catch (Exception ignored) {
                }
            }

            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                ApplicationManager.getApplication().invokeLater(() -> {
                    targetElement = ConfigurationContext.getFromContext(dataContext, ActionPlaces.UNKNOWN).getPsiLocation();
                    targetForm = getTargetForm(project, targetElement);
                    files = hasFocus() ? null : CommonDataKeys.VIRTUAL_FILE_ARRAY.getData(dataContext);
                    if (targetElement != null) {
                        containingFile = targetElement.getContainingFile();
                    }
                });
            }
        }.queue();
    }
    
    public static TargetForm getTargetForm(Project project, PsiElement targetElement) {
        return DumbService.getInstance(project).runReadActionInSmartMode(() -> {
            if (targetElement != null) {
                LSFFormDeclaration formDeclaration = null;
                LSFModuleDeclaration module = null;
                LSFLocalSearchScope localScope = null;

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

                PsiFile containingFile = targetElement.getContainingFile();
                LSFFile file = null;
                if (containingFile instanceof LSFFile) {
                    module = ((LSFFile) containingFile).getModuleDeclaration();
                    file = (LSFFile) containingFile;
                }

                return new TargetForm(formDeclaration, module, localScope, file);
            }
            return null;
        });
    }
    
    private void formUnderCaretChanged(TargetForm targetForm) {
        if (!isLiveDesignEnabled() || !liveDesign.isManualMode()) {
            updateView(targetForm);
        }
    }

    public void updateView(TargetForm targetForm) {
        currentForm = targetForm.form;
        currentModule = targetForm.module;
        
        if (isLiveDesignEnabled()) {
            liveDesign.scheduleRebuild(targetForm.form, targetForm.file);
        } else if (isEmbeddedDesignEnabled()) {
            embeddedDesign.scheduleRebuild(targetForm);
        } else {
            reportsPanel.update(targetForm.form);
        }
    }

    public boolean isLiveDesignEnabled() {
        return getSelectedIndex() == 0;
    }
    
    public boolean isEmbeddedDesignEnabled() {
        return getSelectedIndex() == 1;
    }
    
    public boolean isReportsTabEnabled() {
        return getSelectedIndex() == 2;
    }
    
    public static void openFormUnderCaretDesign(Project project, Consumer<TargetForm> formConsumer) {
        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();

        if (editor != null) {
            DataContext dataContext = DataManager.getInstance().getDataContext(editor.getComponent());

            new Task.Backgroundable(project, "Reading form") {
                PsiElement targetElement;
                TargetForm targetForm;

                @Override
                public void onSuccess() {
                    if (targetForm != null && targetForm.form != null) {
                        formConsumer.accept(targetForm);
                    }
                }

                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    targetElement = DumbService.getInstance(project).runReadActionInSmartMode(() -> ConfigurationContext.getFromContext(dataContext, ActionPlaces.UNKNOWN).getPsiLocation());
                    targetForm = DesignView.getTargetForm(project, targetElement);
                }
            }.queue();
        }
    }
    
    public static class TargetForm {
        public LSFFormDeclaration form;
        public LSFModuleDeclaration module;
        public LSFLocalSearchScope localScope;
        public LSFFile file;
        
        public TargetForm(LSFFormDeclaration form, LSFModuleDeclaration module, LSFLocalSearchScope localScope, LSFFile file) {
            this.form = form;
            this.module = module;
            this.localScope = localScope;
            this.file = file;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TargetForm) {   
                TargetForm tfObj = (TargetForm) obj;
                return tfObj.form == this.form 
                        && tfObj.module == this.module 
                        && BaseUtils.nullEquals(tfObj.localScope, this.localScope) 
                        && BaseUtils.nullEquals(tfObj.file, this.file);
            }
            return false;
        }
    }
}
