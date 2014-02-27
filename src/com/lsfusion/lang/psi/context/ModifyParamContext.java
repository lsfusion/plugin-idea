package com.lsfusion.psi.context;

import com.intellij.psi.PsiElement;

public interface ModifyParamContext extends PsiElement {
    ContextModifier getContextModifier(); // пока просто PsiElement чтобы не разбирать варианты
    
    ContextInferrer getContextInferrer();
}
