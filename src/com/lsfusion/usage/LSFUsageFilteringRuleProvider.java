package com.lsfusion.usage;

import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.usages.ReadWriteAccessUsage;
import com.intellij.usages.ReadWriteAccessUsageInfo2UsageAdapter;
import com.intellij.usages.Usage;
import com.intellij.usages.rules.UsageFilteringRule;
import com.intellij.usages.rules.UsageFilteringRuleProvider;
import com.lsfusion.lang.psi.LSFClassStatement;
import com.lsfusion.lang.psi.LSFFormStatement;
import com.lsfusion.lang.psi.LSFOverrideActionStatement;
import com.lsfusion.lang.psi.LSFOverridePropertyStatement;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;

public class LSFUsageFilteringRuleProvider implements UsageFilteringRuleProvider {
    @NotNull
    @Override
    public Collection<? extends UsageFilteringRule> getApplicableRules(@NotNull Project project) {
        return List.of(new TypeUsageFilteringRule());
    }

    class TypeUsageFilteringRule implements UsageFilteringRule {
        @Override
        public boolean isVisible(@NotNull Usage usage) {
            if (usage instanceof ReadWriteAccessUsage) {
                PsiElement element = ((ReadWriteAccessUsageInfo2UsageAdapter) usage).getElement();
                PsiElement usageElement = LSFPsiUtils.getStatementParent(element);
                if (filterOut(usageElement, element)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean filterOut(PsiElement statement, PsiElement element) {
        if (statement instanceof LSFOverridePropertyStatement && ((LSFOverridePropertyStatement) statement).getMappedPropertyClassParamDeclare().getPropertyUsageWrapper().getPropertyUsage().equals(element) ||
                statement instanceof LSFOverrideActionStatement && ((LSFOverrideActionStatement) statement).getMappedActionClassParamDeclare().getActionUsageWrapper().getActionUsage().equals(element) ||
                statement instanceof LSFFormStatement && ((LSFFormStatement) statement).getExtendingFormDeclaration() != null &&
                        ((LSFFormStatement) statement).getExtendingFormDeclaration().getFormUsageWrapper().getFormUsage().equals(element) ||
                statement instanceof LSFClassStatement && ((LSFClassStatement) statement).getExtendingClassDeclaration() != null &&
                        ((LSFClassStatement) statement).getExtendingClassDeclaration().getCustomClassUsageWrapper() != null &&
                        ((LSFClassStatement) statement).getExtendingClassDeclaration().getCustomClassUsageWrapper().getCustomClassUsage().equals(element)) {
            return true;
        }
        return false;
    }
}
