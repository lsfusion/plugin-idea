package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.lang.psi.declarations.LSFPropDeclaration;

public interface LSFPropReference extends LSFFullNameReference<LSFPropDeclaration, LSFGlobalPropDeclaration> {

    boolean isDirect();

}
