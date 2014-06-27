package com.lsfusion;

import com.intellij.ide.util.DefaultPsiElementCellRenderer;
import com.intellij.psi.PsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.lsfusion.lang.psi.LSFClassStatement;
import com.lsfusion.lang.psi.LSFFormStatement;
import com.lsfusion.lang.psi.LSFOverrideStatement;

public class LSFPsiListCellRenderer extends DefaultPsiElementCellRenderer {
    @Override
    public String getElementText(PsiElement element) {
        LSFOverrideStatement overrideStatement = PsiTreeUtil.getParentOfType(element, LSFOverrideStatement.class);
        if (overrideStatement != null) {
            return getCutText(overrideStatement);
        }

        LSFFormStatement formStatement = PsiTreeUtil.getParentOfType(element, LSFFormStatement.class);
        if (formStatement != null) {
            return getCutText(formStatement);
        }

        LSFClassStatement classStatement = PsiTreeUtil.getParentOfType(element, LSFClassStatement.class);
        if (classStatement != null && classStatement.getExtendingClassDeclaration() != null) {
            return getCutText(classStatement);
        }

        return super.getElementText(element);
    }

    private String getCutText(PsiElement element) {
        String text = element.getText();

        if (text != null) {
            if (text.length() > 60) {
                text = text.substring(0, 60).trim() + "...";
            }

        }
        return text;
    }
}
