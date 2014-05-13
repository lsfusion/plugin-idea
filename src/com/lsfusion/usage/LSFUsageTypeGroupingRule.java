package com.lsfusion.usage;

import com.intellij.openapi.extensions.Extensions;
import com.intellij.openapi.vcs.FileStatus;
import com.intellij.psi.PsiElement;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageGroup;
import com.intellij.usages.UsageTarget;
import com.intellij.usages.UsageView;
import com.intellij.usages.impl.rules.UsageType;
import com.intellij.usages.impl.rules.UsageTypeProvider;
import com.intellij.usages.rules.PsiElementUsage;
import com.intellij.usages.rules.UsageGroupingRuleEx;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFMetaCodeBody;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

public class LSFUsageTypeGroupingRule implements UsageGroupingRuleEx {
    @Nullable
    @Override
    public UsageGroup groupUsage(@NotNull Usage usage) {
        return groupUsage(usage, UsageTarget.EMPTY_ARRAY);
    }
    
    @Nullable
    @Override
    public UsageGroup groupUsage(@NotNull Usage usage, @NotNull UsageTarget[] targets) {
        if (usage instanceof PsiElementUsage) {
            PsiElement psiElement = ((PsiElementUsage)usage).getElement();

            while (psiElement != null) {
                if (psiElement.getParent() instanceof LSFFile || psiElement.getParent() instanceof LSFMetaCodeBody) {
                    break;
                }
                psiElement = psiElement.getParent();
            }

            UsageType usageType = getUsageType(psiElement, targets);
            if (usageType != null) return new LSFUsageTypeGroup(usageType);

            return new LSFUsageTypeGroup(UsageType.UNCLASSIFIED);
        }

        return null;
    }

    @Nullable
    private UsageType getUsageType(PsiElement element, @NotNull UsageTarget[] targets) {
        if (element == null) return null;

        UsageTypeProvider[] providers = Extensions.getExtensions(UsageTypeProvider.EP_NAME);
        for(UsageTypeProvider provider: providers) {
            UsageType usageType;
            if (provider instanceof LSFUsageTypeProvider) {
                usageType = ((LSFUsageTypeProvider) provider).getUsageType(element, targets);
            }
            else {
                usageType = provider.getUsageType(element);
            }
            if (usageType != null) {
                return usageType;
            }
        }

        return null;
    }

    private class LSFUsageTypeGroup implements UsageGroup {
        private UsageType usageType;
        
        public LSFUsageTypeGroup(UsageType usageType) {
            this.usageType = usageType;
        }
        
        @Nullable
        @Override
        public Icon getIcon(boolean isOpen) {
            return null;
        }

        @NotNull
        @Override
        public String getText(@Nullable UsageView view) {
            return usageType != null ? usageType.toString() : "";
        }

        @Nullable
        @Override
        public FileStatus getFileStatus() {
            return null;
        }

        @Override
        public boolean isValid() {
            return true;
        }

        @Override
        public void update() {
        }

        @Override
        public int compareTo(UsageGroup o) {
            return 0;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (!(obj instanceof LSFUsageTypeGroup)) {
                return false;
            }
            LSFUsageTypeGroup usageTypeGroup = (LSFUsageTypeGroup) obj;
            return !(usageType != null ? !usageType.equals(usageTypeGroup.usageType) : usageTypeGroup.usageType != null);
        }

        @Override
        public int hashCode() {
            return usageType != null ? usageType.hashCode() : 0;
        }

        @Override
        public void navigate(boolean requestFocus) {
        }

        @Override
        public boolean canNavigate() {
            return false;
        }

        @Override
        public boolean canNavigateToSource() {
            return false;
        }
    }
}
