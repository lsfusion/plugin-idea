package com.lsfusion;

import com.intellij.codeInsight.navigation.GotoTargetHandler;
import com.intellij.codeInsight.navigation.GotoTargetRendererProvider;
import com.intellij.ide.util.PsiElementListCellRenderer;
import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.LSFCustomClassUsageWrapper;
import com.lsfusion.lang.psi.LSFElement;
import com.lsfusion.lang.psi.LSFFormUsageWrapper;
import com.lsfusion.lang.psi.LSFPropertyUsageWrapper;
import org.jetbrains.annotations.Nullable;

public class LSFGoToTargetRendererProvider implements GotoTargetRendererProvider {
    @Nullable
    @Override
    public PsiElementListCellRenderer getRenderer(PsiElement element, GotoTargetHandler.GotoData gotoData) {
        if (element instanceof LSFElement || element instanceof LSFCustomClassUsageWrapper ||
                element instanceof LSFFormUsageWrapper || element instanceof LSFPropertyUsageWrapper) {
            return new LSFPsiListCellRenderer();
        }
        return null;
    }
}
