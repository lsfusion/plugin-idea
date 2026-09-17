package com.lsfusion.usage;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.psi.PsiElement;
import com.intellij.psi.SmartPointerManager;
import com.intellij.psi.SmartPsiElementPointer;
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
    
    // The usage view asks a group for its text and icon even after the statement has been edited or deleted,
    // so both are taken here, while the PSI is valid, and the statement itself is kept only as a smart pointer
    private static class StatementUsageGroup implements UsageGroup {

        private final SmartPsiElementPointer<PsiElement> statementPointer;
        private final String text;
        private final Icon icon;
        
        public StatementUsageGroup(PsiElement statementElement) {
            statementPointer = SmartPointerManager.getInstance(statementElement.getProject()).createSmartPsiElementPointer(statementElement);
            text = StringUtil.first(statementElement.getText(), 40, true);
            icon = statementElement.getIcon(0);
        }
        
        @Nullable
        @Override
        public Icon getIcon() {
            return icon;
        }

        @NotNull
        @Override
        public String getPresentableGroupText() {
            return text;
        }

        @Nullable
        @Override
        public FileStatus getFileStatus() {
            return null;
        }

        @Override
        public boolean isValid() {
            return statementPointer.getElement() != null;
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
                ((NavigationItem) statementPointer.getElement()).navigate(requestFocus);
            }
        }

        @Override
        public boolean canNavigate() {
            return isValid() && ((NavigationItem) statementPointer.getElement()).canNavigate();
        }

        @Override
        public boolean canNavigateToSource() {
            return true;
        }

        @Override
        public int hashCode() {
            return text.hashCode();
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof StatementUsageGroup)) return false;
            StatementUsageGroup group = (StatementUsageGroup) obj;
            return text.equals(group.text) && SmartPointerManager.getInstance(statementPointer.getProject()).pointToTheSameElement(statementPointer, group.statementPointer);
        }
    }
}
