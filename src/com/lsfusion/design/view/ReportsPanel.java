package com.lsfusion.design.view;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.MessageType;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.psi.PsiFile;
import com.intellij.util.Consumer;
import com.lsfusion.LSFIcons;
import com.lsfusion.design.ui.FlexPanel;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import com.lsfusion.reports.ReportUtils;
import lsfusion.server.physics.dev.debug.DebuggerService;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.io.File;

import static com.lsfusion.debug.DebugUtils.debugProcess;
import static com.lsfusion.debug.DebugUtils.getDebuggerService;

public class ReportsPanel extends FlexPanel {
    private Project project;

    protected boolean wasActivated = false;
    
    private JLabel formNameLabel;
    private final String formLabelFormat = "Form: %s";
    
    private JButton createReportButton;
    private JButton editReportButton;
    private JButton deleteReportButton;

    private LSFFormDeclaration form;
    
    public ReportsPanel(Project project) {
        super(true);
        this.project = project;

        formNameLabel = new JLabel(String.format(formLabelFormat, ""));
        formNameLabel.setBorder(BorderFactory.createEmptyBorder(2, 3, 3, 0));

        createReportButton = new JButton("Create Report", LSFIcons.EDIT_AUTO_REPORT);
        createReportButton.setToolTipText("Create Default Report Design");
        createReportButton.addActionListener(e -> {
            new Task.Backgroundable(project, "Creating report files") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    createReports();
                }
            }.queue();
        });

        editReportButton = new JButton("Edit Report", LSFIcons.EDIT_REPORT);
        editReportButton.setToolTipText("Edit Report Design");
        editReportButton.addActionListener(e -> {
            editReports();
        });

        deleteReportButton = new JButton("Delete Report", LSFIcons.DELETE_REPORT);
        deleteReportButton.setToolTipText("Delete Report Design");
        deleteReportButton.addActionListener(e -> {
            deleteReports(true);
        });
        
        add(formNameLabel);
        add(createReportButton);
        add(editReportButton);
        add(deleteReportButton);
        
        refreshButtons();
    }

    public void onActivated() {
        if (!wasActivated) {
            wasActivated = true;
            DesignView.openFormUnderCaretDesign(project, (Consumer<DesignView.TargetForm>) targetForm -> update(targetForm.form));
        }
    }
    
    public void update(LSFFormDeclaration form) {
        this.form = form;
        formNameLabel.setText(String.format(formLabelFormat, form.getDeclName()));
        refreshButtons();
    }
    
    private boolean hasReportFiles() {
        return form != null && DumbService.getInstance(project).runReadActionInSmartMode(() -> ReportUtils.hasReportFiles(form)); 
    }

    private void refreshButtons() {
        new Task.Backgroundable(project, "Searching for report files") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                refreshButtons(hasReportFiles());
            }
        }.queue();
    }

    private void refreshButtons(boolean hasReportFiles) {
        createReportButton.setText(hasReportFiles ? "Re-create Report" : "Create Report");
        createReportButton.setEnabled(form != null);

        editReportButton.setEnabled(hasReportFiles);

        deleteReportButton.setEnabled(hasReportFiles);
    }
    
    private void createReports() {
        if (debugProcess != null) {
            try {
                if (hasReportFiles()) {
                    deleteReports(false);
                }

                DebuggerService debuggerService = getDebuggerService();
                if (debuggerService != null) {
                    String reportFiles = (String) debuggerService.evalServer("run() {createAutoReport('" +
                            DumbService.getInstance(project).runReadActionInSmartMode(() -> form.getCanonicalName()) + 
                            "');}");
                    if (reportFiles != null) {
                        for (String file : reportFiles.split(";")) {
                            Desktop.getDesktop().open(new File(file));
                        }
                        refreshButtons(true);
                    }
                }
            } catch (Exception ex) {
                ApplicationManager.getApplication().invokeLater(() -> JBPopupFactory.getInstance().createMessage("Can't create report files: " + ex.getMessage()).show(createReportButton));
            }
        } else {
            JBPopupFactory.getInstance().createHtmlTextBalloonBuilder("Start server to create reports", MessageType.ERROR, null).createBalloon().showInCenterOf(createReportButton);
        }
    }

    private void editReports() {
        new Task.Backgroundable(project, "Searching for report files") {
            @Override
            public void run(@NotNull ProgressIndicator indicator) {
                for (PsiFile file : ReportUtils.findReportFiles(form)) {
                    String path = file.getVirtualFile().getCanonicalPath();
                    if(path != null) {
                        try {
                            Desktop.getDesktop().open(new File(path));
                        } catch (Exception ex) {
                            JBPopupFactory.getInstance().createMessage("Can't open report files: " + ex.getMessage()).show(editReportButton);
                        }
                    }
                }
            }
        }.queue();
    }

    private void deleteReports(boolean background) {
        Runnable deleteRunnable = () -> {
            if (JOptionPane.showConfirmDialog(JOptionPane.getRootFrame(),
                    "Are you sure you want to delete existing report design?", "Jasper reports", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
                for (PsiFile file : ReportUtils.findReportFiles(form)) {
                    String path = file.getVirtualFile().getCanonicalPath();
                    if (path != null) {
                        if (new File(path).delete()) {
                            refreshButtons(false);
                        }
                    }
                }
            }
        };

        if (background) {
            new Task.Backgroundable(project, "Searching for report files") {
                @Override
                public void run(@NotNull ProgressIndicator indicator) {
                    deleteRunnable.run();
                }
            }.queue();
        } else {
            deleteRunnable.run();
        }
    }
}
