package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.documentation.LSFDocumentation;

public interface ModifyParamContext extends PsiElement, LSFDocumentation {
    ContextModifier getContextModifier(); // пока просто PsiElement чтобы не разбирать варианты
    
    ContextInferrer getContextInferrer();
}
