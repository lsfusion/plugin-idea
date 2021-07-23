package com.lsfusion.lang.psi.extend;

import com.lsfusion.documentation.LSFDocumentation;
import com.lsfusion.lang.psi.LSFId;
import com.lsfusion.lang.psi.declarations.LSFComponentDeclaration;
import com.lsfusion.lang.psi.declarations.LSFDeclaration;
import com.lsfusion.lang.psi.stubs.extend.DesignStubElement;

import java.util.Collection;
import java.util.Set;

public interface LSFDesign extends LSFExtend<LSFDesign, DesignStubElement>, LSFDocumentation {
    Collection<LSFComponentDeclaration> getComponentDecls();
    
    LSFId getFormUsageNameIdentifier();
}