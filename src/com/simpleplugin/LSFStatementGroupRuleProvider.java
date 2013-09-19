package com.simpleplugin;

import com.intellij.openapi.project.Project;
import com.intellij.usages.impl.FileStructureGroupRuleProvider;
import com.intellij.usages.rules.UsageGroupingRule;
import org.jetbrains.annotations.Nullable;

public class LSFStatementGroupRuleProvider implements FileStructureGroupRuleProvider {
    @Nullable
    @Override
    public UsageGroupingRule getUsageGroupingRule(Project project) {
        return new LSFStatementGroupingRule();
    }
}
