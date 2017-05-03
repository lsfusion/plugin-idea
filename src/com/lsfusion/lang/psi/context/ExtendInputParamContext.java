package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;

public interface ExtendInputParamContext extends PsiElement {
    ContextModifier getInputContextModifier(); // пока просто PsiElement чтобы не разбирать варианты
    
    ContextInferrer getInputContextInferrer();
}
