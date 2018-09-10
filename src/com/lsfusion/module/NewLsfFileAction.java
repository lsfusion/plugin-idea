package com.lsfusion.module;

import com.intellij.ide.actions.CreateFileFromTemplateAction;
import com.intellij.ide.actions.CreateFileFromTemplateDialog;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDirectory;
import com.lsfusion.LSFBundle;
import com.lsfusion.LSFIcons;

import static com.lsfusion.module.LSFusionTemplates.TEMPLATE_LSFUSION_MODULE;

public class NewLsfFileAction extends CreateFileFromTemplateAction implements DumbAware {
    public NewLsfFileAction() {
        super(LSFBundle.message("newfile.action.menu.text"), LSFBundle.message("newfile.action.menu.description"), LSFIcons.FILE);
    }

    @Override
    protected void buildDialog(Project project, PsiDirectory directory, CreateFileFromTemplateDialog.Builder builder) {
        builder
                .setTitle(LSFBundle.message("newfile.action.dlg.title"))
                .addKind(LSFBundle.message("newfile.action.menu.text"), LSFIcons.FILE, TEMPLATE_LSFUSION_MODULE);
    }

    @Override
    protected String getActionName(PsiDirectory directory, String newName, String templateName) {
        return LSFBundle.message("newfile.action.menu.description");
    }
}
