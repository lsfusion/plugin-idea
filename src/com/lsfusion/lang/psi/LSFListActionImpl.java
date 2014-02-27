package com.lsfusion.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.PsiElement;
import com.intellij.psi.ResolveState;
import com.intellij.psi.scope.PsiScopeProcessor;
import org.jetbrains.annotations.NotNull;

public abstract class LSFListActionImpl extends LSFElementImpl implements LSFListAction {

    public LSFListActionImpl(@NotNull ASTNode node) {
        super(node);
    }

    @Override
    public boolean processDeclarations(@NotNull PsiScopeProcessor processor, @NotNull ResolveState state, PsiElement lastParent, @NotNull PsiElement place) {
        for(PsiElement child : getChildren()) {
            if(child.equals(lastParent))
                break; 
            if(child instanceof LSFLocalDataPropertyDefinition)
                if(!processor.execute(child, state))
                    return false;
        }
        return super.processDeclarations(processor, state, lastParent, place);
    }
}
