package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;

public interface ExtendDoParamContext extends PsiElement {
    ContextModifier getDoContextModifier(); // пока просто PsiElement чтобы не разбирать варианты
    
    ContextInferrer getDoContextInferrer();
}
