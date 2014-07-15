package com.lsfusion.usage;

import com.intellij.find.FindBundle;
import com.intellij.find.findUsages.CommonFindUsagesDialog;
import com.intellij.find.findUsages.FindUsagesHandler;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.StateRestoringCheckBox;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LSFFindUsagesDialog extends CommonFindUsagesDialog {
    private StateRestoringCheckBox usagesCB;
    private StateRestoringCheckBox implementationsCB;

    public LSFFindUsagesDialog(@NotNull PsiElement element, @NotNull Project project, @NotNull LSFFindUsagesOptions findUsagesOptions, boolean toShowInNewTab, boolean mustOpenInNewTab, boolean isSingleFile, FindUsagesHandler handler) {
        super(element, project, findUsagesOptions, toShowInNewTab, mustOpenInNewTab, isSingleFile, handler);
    }

    @Override
    public void calcFindUsagesOptions(FindUsagesOptions options) {
        if (options instanceof LSFFindUsagesOptions) {
            if (isToChange(implementationsCB)) {
                ((LSFFindUsagesOptions) options).isImplementingMethods = isSelected(implementationsCB);
            }
        }
        if (isToChange(usagesCB)) {
            options.isUsages = isSelected(usagesCB);
        }
        super.calcFindUsagesOptions(options);
    }

    public LSFFindUsagesOptions getFindUsagesOptions() {
        return (LSFFindUsagesOptions) myFindUsagesOptions;
    }

    @Override
    protected JPanel createFindWhatPanel() {
        JPanel findWhatPanel = new JPanel();
        findWhatPanel.setBorder(IdeBorderFactory.createTitledBorder(FindBundle.message("find.what.group"), true));
        findWhatPanel.setLayout(new BoxLayout(findWhatPanel, BoxLayout.Y_AXIS));

        usagesCB = addCheckboxToPanel("Usages", getFindUsagesOptions().isUsages, findWhatPanel, true);
        implementationsCB = addCheckboxToPanel("Implementations", getFindUsagesOptions().isImplementingMethods, findWhatPanel, true);

        return findWhatPanel;
    }
}
