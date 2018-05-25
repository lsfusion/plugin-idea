package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.declarations.LSFExprParamDeclaration;

import java.util.*;

public abstract class ElementsContextModifier implements ContextModifier {

    protected abstract List<? extends PsiElement> getElements();

    private static void recResolveParams(PsiElement modifier, int offset, Set<String> foundParams, Set<String> usedParams, List<LSFExprParamDeclaration> extParams) {
        if(modifier.getTextOffset() > offset) // если идет после искомого элемента, не интересует
            return;

        if (modifier instanceof LSFExprParamDeclaration) {
            LSFExprParamDeclaration param = (LSFExprParamDeclaration)modifier;
            String paramName = param.getDeclName();
            if(foundParams != null && foundParams.add(paramName))
                extParams.add((LSFExprParamDeclaration) modifier);
            if(usedParams != null)
                usedParams.add(paramName);
        }
        if(foundParams != null && modifier instanceof ModifyParamContext) // hardcode конечно, но иначе придется все вручную делать 
            return;
        for (PsiElement child : modifier.getChildren())
            recResolveParams(child, offset, foundParams, usedParams, extParams);
    }
    
    // по идее должен вызываться если подразумевается отсутствие (или специальная обработка используется) внешнего контекста
    public List<LSFExprParamDeclaration> resolveParams(int offset, Set<LSFExprParamDeclaration> currentParams) {
        Set<String> paramNames = new HashSet<>();
        for(LSFExprParamDeclaration currentParam : currentParams)
            paramNames.add(currentParam.getDeclName());

        List<LSFExprParamDeclaration> extParams = new ArrayList<>();
        for(PsiElement element : getElements())
            recResolveParams(element, offset, paramNames, null, extParams);
        return extParams;
    }

    // вообще orderedHashSet
    public Set<String> resolveAllParams() {
        Set<String> usedParams = new LinkedHashSet<>();
        for(PsiElement element : getElements())
            recResolveParams(element, Integer.MAX_VALUE, null, usedParams, null);
        return usedParams;
    }
}
