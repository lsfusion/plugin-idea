package com.simpleplugin.psi.context;

import com.intellij.psi.PsiElement;

import java.util.List;

public interface ModifyParamContext extends PsiElement {
    ContextModifier getContextModifier(); // пока просто PsiElement чтобы не разбирать варианты
    
    ContextInferrer getContextInferrer();
}
