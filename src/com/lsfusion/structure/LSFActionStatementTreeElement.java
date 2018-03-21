package com.lsfusion.structure;

import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFActionStatementTreeElement extends LSFActionOrPropertyStatementTreeElement<LSFActionDeclaration> {

    public LSFActionStatementTreeElement(@NotNull LSFValueClass paramClass, LSFActionDeclaration element, LSFStructureViewNavigationHandler navigationHandler) {
        super(paramClass, element, navigationHandler);
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return LSFPsiUtils.getPresentableText(getElement());
    }
}
