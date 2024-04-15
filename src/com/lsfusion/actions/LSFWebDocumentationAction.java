package com.lsfusion.actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowManager;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.ui.jcef.JBCefBrowser;
import com.intellij.ui.popup.AbstractPopup;
import com.lsfusion.documentation.LSFDocumentationProvider;
import com.lsfusion.lang.psi.LSFFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;

public class LSFWebDocumentationAction extends AnAction {
    @SuppressWarnings("UnstableApiUsage")
    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
        Project project = getEventProject(e);
        if (project != null) {
            // With quick docs shown cleaner solution would be to get target psi element from targetPointer in DocumentationBrowser
            // but this method changes IDEA compatibility range so for now we just get it from editor again
            // DocumentationBrowser browser = (DocumentationBrowser) e.getDataContext().getData("documentation.browser");
            // PsiElementDocumentationTarget documentationTarget = (PsiElementDocumentationTarget)browser.getTargetPointer().dereference();
            // PsiElement element = ((PsiElementDocumentationTarget) documentationTarget).getTargetElement();
            
            String url = getEditorElementDocUrl(project);
            if (url != null) {
                Object popup = e.getDataContext().getData("documentation.popup");
                if (popup instanceof AbstractPopup) {
                    ((AbstractPopup) popup).setUiVisible(false);
                }
                hideToolWindow(project);
                
                WebDocumentationDialog.showDialog(project, url);  
            }
        }
    }
    
    private String getEditorElementDocUrl(Project project) {
        Editor selectedTextEditor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (selectedTextEditor != null) {
            PsiFile file = PsiDocumentManager.getInstance(project).getPsiFile(selectedTextEditor.getDocument());
            if (file instanceof LSFFile) {
                PsiElement element = file.findElementAt(selectedTextEditor.getCaretModel().getOffset());
                if (element != null) {
                    return LSFDocumentationProvider.getDocumentationURL(element);
                }
            }
        }
        return null;
    }
    
    private void hideToolWindow(Project project) {
        ToolWindow documentationToolWindow = ToolWindowManager.getInstance(project).getToolWindow("documentation.v2");
        if (documentationToolWindow != null) {
            documentationToolWindow.hide();
        }
    }

    public static final class WebDocumentationDialog extends DialogWrapper {
        private final JComponent myContentPanel;

        private WebDocumentationDialog(String title, Project project, String url) {
            super(project, true);
            setTitle(title);

            JBCefBrowser browser = new JBCefBrowser(url);
            myContentPanel = browser.getComponent();
            //noinspection UseJBColor
            myContentPanel.setBackground(Color.WHITE);
            
            init();
        }

        @Override
        protected JComponent createCenterPanel() {
            return myContentPanel;
        }

        public static void showDialog(Project project, String url) {
            new WebDocumentationDialog("LSF Documentation", project, url).show();
        }
    }
}
