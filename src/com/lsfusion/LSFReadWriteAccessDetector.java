package com.lsfusion;

import com.intellij.codeInsight.highlighting.ReadWriteAccessDetector;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.lsfusion.lang.LSFFileType;
import com.lsfusion.lang.psi.*;

public class LSFReadWriteAccessDetector extends ReadWriteAccessDetector {
    @Override
    public boolean isReadWriteAccessible(PsiElement element) {
        return element.getContainingFile().getFileType() == LSFFileType.INSTANCE;
    }

    @Override
    public boolean isDeclarationWriteAccess(PsiElement element) {
        return false;
    }

    @Override
    public Access getReferenceAccess(PsiElement referencedElement, PsiReference reference) {
        return getExpressionAccess(reference.getElement());
    }

    @Override
    public Access getExpressionAccess(PsiElement expression) {
        PsiElement element = expression;
        while (element != null) {
            if (element.getParent() != null) {
                if (element.getParent() instanceof LSFAssignActionPropertyDefinitionBody && element instanceof LSFMappedPropertyExprParam) {
                    return Access.Write;
                }
                if (element.getParent() instanceof LSFWriteWhenStatement && element instanceof LSFMappedPropertyClassParamDeclare) {
                    return Access.Write;
                }
            }

            element = element.getParent();
        }
        
        return Access.Read;
    }
}
