package com.simpleplugin.psi.references;

import com.simpleplugin.psi.declarations.LSFGlobalPropDeclaration;
import com.simpleplugin.psi.declarations.LSFPropDeclaration;

public interface LSFPropReference extends LSFFullNameReference<LSFPropDeclaration, LSFGlobalPropDeclaration> {

    boolean isDirect();

}
