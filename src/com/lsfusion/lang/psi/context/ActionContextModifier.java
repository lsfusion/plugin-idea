package com.lsfusion.lang.psi.context;

import com.intellij.psi.PsiElement;
import com.lsfusion.lang.psi.LSFListActionPropertyDefinitionBody;

import java.util.Collections;
import java.util.List;

public class ActionContextModifier extends ElementsContextModifier {

    private final LSFListActionPropertyDefinitionBody listAction;

    public ActionContextModifier(LSFListActionPropertyDefinitionBody listAction) {
        this.listAction = listAction;
    }

    protected List<? extends PsiElement> getElements() {
        return Collections.singletonList(listAction);
    }
}
