package com.lsfusion.lang.psi.declarations;

import com.lsfusion.documentation.LSFDocumentation;
import com.lsfusion.lang.psi.stubs.ActionStubElement;

public interface LSFActionDeclaration extends LSFActionOrGlobalPropDeclaration<LSFActionDeclaration, ActionStubElement>, LSFFormOrActionDeclaration<LSFActionDeclaration, ActionStubElement>, LSFActionOrPropDeclaration, LSFDocumentation {
}
