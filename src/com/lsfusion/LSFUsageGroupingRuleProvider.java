package com.lsfusion;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;
import com.intellij.usages.UsageView;
import com.intellij.usages.UsageViewSettings;
import com.intellij.usages.rules.UsageGroupingRule;
import com.intellij.usages.rules.UsageGroupingRuleProvider;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class LSFUsageGroupingRuleProvider implements UsageGroupingRuleProvider {
    @NotNull
    @Override
    public UsageGroupingRule[] getActiveRules(Project project) {
        List<UsageGroupingRule> rules = new ArrayList<UsageGroupingRule>();
        if (UsageViewSettings.getInstance().GROUP_BY_USAGE_TYPE) {
            rules.add(new LSFUsageTypeGroupingRule());
        }
        return rules.toArray(new UsageGroupingRule[rules.size()]);
    }

    @NotNull
    @Override
    public AnAction[] createGroupingActions(UsageView view) {
        return new AnAction[0];
    }
}
