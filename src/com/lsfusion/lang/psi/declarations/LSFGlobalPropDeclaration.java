package com.lsfusion.psi.declarations;

import com.lsfusion.psi.stubs.PropStubElement;

public interface LSFGlobalPropDeclaration extends LSFFullNameDeclaration<LSFGlobalPropDeclaration, PropStubElement>, LSFPropDeclaration {

    String getCaption();

    boolean isAction();
}
