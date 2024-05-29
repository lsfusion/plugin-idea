package com.lsfusion.usage;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.psi.PsiElement;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageGroup;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.rules.PsiElementUsage;
import com.intellij.usages.rules.SingleParentUsageGroupingRule;
import com.intellij.usages.rules.UsageGroupingRule;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LSFStatementGroupingRule extends SingleParentUsageGroupingRule implements UsageGroupingRule {
    public static final LSFStatementGroupingRule INSTANCE = new LSFStatementGroupingRule();

    @Override
    protected @Nullable UsageGroup getParentGroupFor(@NotNull Usage usage, UsageTarget[] targets) {
        if (!(usage instanceof PsiElementUsage)) return null;

        PsiElement element = LSFPsiUtils.getStatementParent(((PsiElementUsage)usage).getElement());

        return element == null ? null : new StatementUsageGroup(element);
    }
    
    private class StatementUsageGroup implements UsageGroup {

        public PsiElement statementElement;
        
        public StatementUsageGroup(PsiElement statementElement) {
            this.statementElement = statementElement;
        }
        
        @Nullable
        @Override
        public Icon getIcon() {
            return DumbService.getInstance(statementElement.getProject()).runReadActionInSmartMode(() -> statementElement.getIcon(0));
        }

        @NotNull
        @Override
        public String getPresentableGroupText() {
            return DumbService.getInstance(statementElement.getProject()).runReadActionInSmartMode(() -> {
                String text = statementElement.getText();
                if (text.length() <= 40) {
                    return text;
                } else {
                    return text.substring(0, 40) + "...";
                }    
            });
        }

        @Nullable
        @Override
        public FileStatus getFileStatus() {
            return null;
        }

        @Override
        public boolean isValid() {
            return statementElement.isValid();
        }

        @Override
        public void update() {
        }

        @Override
        public int compareTo(UsageGroup o) {
            return getPresentableGroupText().compareToIgnoreCase(o.getPresentableGroupText());
        }

        @Override
        public void navigate(boolean requestFocus) {
            if (canNavigate()) {
                ((NavigationItem) statementElement).navigate(requestFocus);
            }
        }

        @Override
        public boolean canNavigate() {
            return isValid() && ((NavigationItem) statementElement).canNavigate();
        }

        @Override
        public boolean canNavigateToSource() {
            return true;
        }

        @Override
        public int hashCode() {
            return statementElement.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            return obj instanceof StatementUsageGroup && statementElement.equals(((StatementUsageGroup) obj).statementElement);
        }
    }
}
