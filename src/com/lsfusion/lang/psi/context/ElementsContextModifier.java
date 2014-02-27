package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class ElementsContextModifier implements ContextModifier {

    protected abstract List<? extends PsiElement> getElements();

    private static void recResolveParams(PsiElement modifier, int offset, Set<String> foundParams, List<LSFExprParamDeclaration> extParams) {
        if(modifier.getTextOffset() > offset) // если идет после искомого элемента, не интересует
            return;

        if (modifier instanceof LSFExprParamDeclaration) {
            LSFExprParamDeclaration param = (LSFExprParamDeclaration)modifier;
            if(foundParams.add(param.getDeclName()))
                extParams.add((LSFExprParamDeclaration) modifier);
        }
        if (!(modifier instanceof ModifyParamContext)) { // hardcode конечно, но иначе придется все вручную делать
            for (PsiElement child : modifier.getChildren())
                recResolveParams(child, offset, foundParams, extParams);
        }
    }
    
    public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams) {
        Set<String> paramNames = new HashSet<String>();
        for(LSFExprParamDeclaration currentParam : currentParams)
            paramNames.add(currentParam.getDeclName());

        List<LSFExprParamDeclaration> extParams = new ArrayList<LSFExprParamDeclaration>();
        for(PsiElement element : getElements())
            recResolveParams(element, offset, paramNames, extParams);
        return extParams;

    }
}
