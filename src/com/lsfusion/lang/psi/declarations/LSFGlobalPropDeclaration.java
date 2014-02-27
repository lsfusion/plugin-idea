package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.stubs.PropStubElement;

public interface LSFGlobalPropDeclaration extends LSFFullNameDeclaration<LSFGlobalPropDeclaration, PropStubElement>, LSFPropDeclaration {

    String getCaption();

    boolean isAction();
}
