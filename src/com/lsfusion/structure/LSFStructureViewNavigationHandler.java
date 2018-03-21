package com.lsfusion.structure;

import com.lsfusion.lang.psi.declarations.LSFActionOrPropDeclaration;

public interface LSFStructureViewNavigationHandler {
    <T extends LSFActionOrPropDeclaration> void navigate(LSFActionOrPropertyStatementTreeElement<T> element, boolean requestFocus);

}
