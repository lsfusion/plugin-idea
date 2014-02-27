package com.lsfusion.psi.references;

import com.lsfusion.psi.declarations.LSFGlobalPropDeclaration;
import com.lsfusion.psi.declarations.LSFPropDeclaration;

public interface LSFPropReference extends LSFFullNameReference<LSFPropDeclaration, LSFGlobalPropDeclaration> {

    boolean isDirect();

}
