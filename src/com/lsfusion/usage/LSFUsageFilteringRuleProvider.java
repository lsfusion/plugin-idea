package com.lsfusion.usage;

import com.intellij.execution.actions.ConfigurationContext;
import com.intellij.ide.impl.DataManagerImpl;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.usages.ReadWriteAccessUsage;
import com.intellij.usages.ReadWriteAccessUsageInfo2UsageAdapter;
import com.intellij.usages.Usage;
import com.intellij.usages.UsageView;
import com.intellij.usages.rules.UsageFilteringRule;
import com.intellij.usages.rules.UsageFilteringRuleProvider;
import com.lsfusion.hierarchy.usages.LSFUsageHierarchyBrowser;
import com.lsfusion.lang.psi.LSFClassStatement;
import com.lsfusion.lang.psi.LSFExplicitInterfacePropertyStatement;
import com.lsfusion.lang.psi.LSFFormStatement;
import com.lsfusion.lang.psi.LSFOverrideStatement;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LSFUsageFilteringRuleProvider implements UsageFilteringRuleProvider {
    private PsiElement sourceStatement;

    @NotNull
    @Override
    public UsageFilteringRule[] getActiveRules(@NotNull Project project) {
        List<UsageFilteringRule> rules = new ArrayList<UsageFilteringRule>();
        rules.add(new TypeUsageFilteringRule());

        Editor editor = FileEditorManager.getInstance(project).getSelectedTextEditor();
        if (editor != null) {
            DataContext dataContext = new DataManagerImpl.MyDataContext(editor.getComponent());
            PsiElement targetElement = ConfigurationContext.getFromContext(dataContext).getPsiLocation();
            if (targetElement != null) {
                sourceStatement = LSFUsageHierarchyBrowser.findSourceStatement(targetElement);
            }
        }

        return rules.toArray(new UsageFilteringRule[rules.size()]);
    }

    @NotNull
    @Override
    public AnAction[] createFilteringActions(@NotNull UsageView view) {
        return new AnAction[0];
    }

    class TypeUsageFilteringRule implements UsageFilteringRule {
        @Override
        public boolean isVisible(@NotNull Usage usage) {
            if (sourceStatement != null && usage instanceof ReadWriteAccessUsage) {
                PsiElement usageElement = LSFPsiUtils.getStatementParent(((ReadWriteAccessUsageInfo2UsageAdapter) usage).getElement());
                if (filterOut(sourceStatement, usageElement)) {
                    return false;
                }
            }
            return true;
        }
    }

    public static boolean filterOut(PsiElement parent, PsiElement child) {
        if (parent instanceof LSFExplicitInterfacePropertyStatement && child instanceof LSFOverrideStatement &&
                ((LSFExplicitInterfacePropertyStatement) parent).getPropertyStatement().getName().equals(((LSFOverrideStatement) child).getMappedPropertyClassParamDeclare().getPropertyUsage().getName()) ||
                parent instanceof LSFFormStatement && (child instanceof LSFFormStatement && ((LSFFormStatement) child).getExtendingFormDeclaration() != null &&
                        ((LSFFormStatement) parent).resolveFormDecl().equals(((LSFFormStatement) child).resolveFormDecl())) ||
                parent instanceof LSFClassStatement && (child instanceof LSFClassStatement && ((LSFClassStatement) child).getExtendingClassDeclaration() != null)) {
            return true;
        }
        return false;
    }
}
