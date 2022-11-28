package com.lsfusion.lang.psi.declarations;

import com.lsfusion.documentation.LSFDocumentation;
import com.lsfusion.lang.psi.stubs.StatementActionStubElement;
import com.lsfusion.lang.psi.stubs.StatementPropStubElement;

//по аналогии с LSFStatementGlobalPropDeclaration
public interface LSFStatementActionDeclaration extends LSFActionDeclaration<LSFStatementActionDeclaration, StatementActionStubElement>, LSFFormOrActionDeclaration<LSFStatementActionDeclaration, StatementActionStubElement>, LSFActionOrPropDeclaration, LSFDocumentation {
}
