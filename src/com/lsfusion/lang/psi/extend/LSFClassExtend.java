package com.lsfusion.lang.psi.extend;

import com.lsfusion.documentation.LSFDocumentation;
import com.lsfusion.mcp.LSFMCPDeclaration;
import com.lsfusion.lang.psi.LSFStringClassRef;
import com.lsfusion.lang.psi.declarations.LSFClassDeclaration;
import com.lsfusion.lang.psi.declarations.LSFStaticObjectDeclaration;
import com.lsfusion.lang.psi.stubs.extend.ExtendClassStubElement;

import java.util.List;

public interface LSFClassExtend extends LSFExtend<LSFClassExtend, ExtendClassStubElement>, LSFDocumentation, LSFMCPDeclaration {
    
    List<LSFClassDeclaration> resolveExtends();

    LSFStringClassRef getThis();

    List<LSFStringClassRef> getExtends();
    
    List<LSFStaticObjectDeclaration> getStaticObjects();
}
