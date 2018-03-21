package com.lsfusion.structure;

import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class LSFPropertyStatementTreeElement extends LSFActionOrPropertyStatementTreeElement<LSFGlobalPropDeclaration> {
   
    public LSFPropertyStatementTreeElement(@NotNull LSFValueClass paramClass, LSFGlobalPropDeclaration element, LSFStructureViewNavigationHandler navigationHandler) {
        super(paramClass, element, navigationHandler);
    }

    @Nullable
    @Override
    public String getPresentableText() {
        return LSFPsiUtils.getPresentableText(getElement());
    }
}
