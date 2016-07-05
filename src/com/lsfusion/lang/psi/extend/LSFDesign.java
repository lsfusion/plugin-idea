package com.lsfusion.lang.psi.extend;

import com.lsfusion.lang.psi.declarations.LSFComponentDeclaration;
import com.lsfusion.lang.psi.stubs.extend.DesignStubElement;

import java.util.Collection;

public interface LSFDesign extends LSFExtend<LSFDesign, DesignStubElement> {
    Collection<LSFComponentDeclaration> getComponentDecls();
}