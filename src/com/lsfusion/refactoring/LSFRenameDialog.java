package com.lsfusion.refactoring;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.ComboBox;
import com.intellij.psi.PsiElement;
import com.intellij.refactoring.rename.RenameDialog;
import com.intellij.ui.ListCellRendererWrapper;
import com.intellij.ui.NonFocusableCheckBox;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import static com.lsfusion.refactoring.MigrationChangePolicy.*;

class LSFRenameDialog extends RenameDialog {

    private final LSFRenameFullNameProcessor renameProcessor;
    private ComboBox cbVersionChangePolicy;
    private NonFocusableCheckBox cbChangeMigrationFile;

    public LSFRenameDialog(Project project, PsiElement element, PsiElement nameSuggestionContext, Editor editor, LSFRenameFullNameProcessor renameProcessor) {
        super(project, element, nameSuggestionContext, editor);
        this.renameProcessor = renameProcessor;
    }

    @Override
    protected void createCheckboxes(JPanel panel, GridBagConstraints gbConstraints) {
        super.createCheckboxes(panel, gbConstraints);

        if (isMigrationNeeded()) {
            gbConstraints.insets = new Insets(0, 0, 4, 0);
            gbConstraints.gridwidth = GridBagConstraints.REMAINDER;
            gbConstraints.gridx = 0;
            gbConstraints.weighty = 0;
            gbConstraints.weightx = 1;
            gbConstraints.fill = GridBagConstraints.BOTH;

            cbChangeMigrationFile = new NonFocusableCheckBox("Change migration file");
            cbChangeMigrationFile.setSelected(true);
            cbChangeMigrationFile.addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    cbVersionChangePolicy.setEnabled(cbChangeMigrationFile.isSelected());
                }
            });
            panel.add(cbChangeMigrationFile, gbConstraints);

            cbVersionChangePolicy = new PolicyComboBox();
            panel.add(cbVersionChangePolicy, gbConstraints);
        }
    }

    private boolean isMigrationNeeded() {
        return LSFRenameFullNameProcessor.isMigrationNeeded(getPsiElement());
    }

    public MigrationChangePolicy getMigrationPolicy() {
        return cbChangeMigrationFile != null && cbChangeMigrationFile.isSelected()
               ? (MigrationChangePolicy) cbVersionChangePolicy.getSelectedItem()
               : null;

    }

    @Override
    protected void doAction() {
        renameProcessor.setMigrationPolicy(getMigrationPolicy());
        super.doAction();
        
    }

    private static class PolicyComboBox extends ComboBox {
        public PolicyComboBox() {
            super(new MigrationChangePolicy[]{USE_LAST_VERSION, INCREMENT_VERSION, INCREMENT_VERSION_IF_COMMITED});

            setSelectedItem(INCREMENT_VERSION_IF_COMMITED);
            
            setRenderer(new ListCellRendererWrapper() {
                @Override
                public void customize(JList list, Object value, int index, boolean selected, boolean hasFocus) {
                    if (value instanceof MigrationChangePolicy) {
                        setText(((MigrationChangePolicy) value).getDisplayText());
                    } else if (value instanceof String) {
                        setText((String) value);
                    }
                }
            });
        }


    }
}
