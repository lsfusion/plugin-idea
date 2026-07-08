package com.lsfusion.usage;

import com.intellij.find.findUsages.AbstractFindUsagesDialog;
import com.intellij.find.findUsages.FindUsagesOptions;
import com.intellij.lang.findUsages.DescriptiveNameUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiElement;
import com.intellij.ui.IdeBorderFactory;
import com.intellij.ui.SimpleColoredComponent;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.StateRestoringCheckBox;
import com.intellij.usageView.UsageViewUtil;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class LSFFindUsagesDialog extends AbstractFindUsagesDialog {
    private StateRestoringCheckBox usagesCB;
    private StateRestoringCheckBox implementationsCB;
    private PsiElement myPsiElement;
    // precomputed by LSFFindUsagesHandler: getUseScope is a slow operation and must not run
    // in isInFileOnly(), which init() calls on the EDT (SlowOperations assertion, IDEA-347939)
    private final boolean isLocalUseScope;

    public LSFFindUsagesDialog(@NotNull PsiElement element, @NotNull Project project, @NotNull LSFFindUsagesOptions findUsagesOptions, boolean toShowInNewTab, boolean mustOpenInNewTab, boolean isSingleFile, boolean searchForTextOccurrencesAvailable, boolean isLocalUseScope) {
        super(project, findUsagesOptions, toShowInNewTab, mustOpenInNewTab, isSingleFile, searchForTextOccurrencesAvailable, true);
        myPsiElement = element;
        this.isLocalUseScope = isLocalUseScope;
        init();
    }

    // c/p from CommonFindUsagesDialog
    @Override
    public void configureLabelComponent(@NotNull SimpleColoredComponent coloredComponent) {
        coloredComponent.append(StringUtil.capitalize(UsageViewUtil.getType(myPsiElement)));
        coloredComponent.append(" ");
        coloredComponent.append(DescriptiveNameUtil.getDescriptiveName(myPsiElement), SimpleTextAttributes.REGULAR_BOLD_ATTRIBUTES);    
    }

    // c/p from CommonFindUsagesDialog
    @Override
    protected boolean isInFileOnly() {
        return super.isInFileOnly() || isLocalUseScope;
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
        findWhatPanel.setBorder(IdeBorderFactory.createTitledBorder("Find What", true));
        findWhatPanel.setLayout(new BoxLayout(findWhatPanel, BoxLayout.Y_AXIS));

        usagesCB = addCheckboxToPanel("Usages", getFindUsagesOptions().isUsages, findWhatPanel, true);
        implementationsCB = addCheckboxToPanel("Implementations", getFindUsagesOptions().isImplementingMethods, findWhatPanel, true);

        return findWhatPanel;
    }
}
