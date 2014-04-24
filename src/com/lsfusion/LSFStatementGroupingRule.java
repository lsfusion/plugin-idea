package com.lsfusion;

import com.intellij.navigation.NavigationItem;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.psi.PsiElement;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageGroup;
import com.intellij.usages.UsageView;
import com.intellij.usages.rules.PsiElementUsage;
import com.intellij.usages.rules.UsageGroupingRule;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFMetaCodeBody;
import com.lsfusion.lang.psi.LSFScriptStatement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LSFStatementGroupingRule implements UsageGroupingRule {
    @Nullable
    @Override
    public UsageGroup groupUsage(@NotNull Usage usage) {
        if (!(usage instanceof PsiElementUsage)) return null;
        PsiElement psiElement = ((PsiElementUsage)usage).getElement();
        
        while (psiElement != null) {
            if (psiElement instanceof LSFFile || psiElement instanceof LSFMetaCodeBody || psiElement instanceof LSFScriptStatement) {
                break;
            }
            psiElement = psiElement.getParent();
        }
        
        return psiElement != null ? new StatementUsageGroup(psiElement) : null;
    }
    
    private class StatementUsageGroup implements UsageGroup {

        public PsiElement statementElement;
        
        public StatementUsageGroup(PsiElement statementElement) {
            this.statementElement = statementElement;
        }
        
        @Nullable
        @Override
        public Icon getIcon(boolean isOpen) {
            return statementElement.getIcon(0);
        }

        @NotNull
        @Override
        public String getText(@Nullable UsageView view) {
            String text = statementElement.getText(); 
            if (text.length() <= 40) {
                return text;
            } else {
                return text.substring(0, 40) + "...";
            }
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
            return 0;
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
