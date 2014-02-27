package com.lsfusion.lang.psi.references;

import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.psi.declarations.LSFGroupObjectDeclaration;

import java.util.List;

public interface LSFGroupObjectReference extends LSFFormElementReference<LSFGroupObjectDeclaration> {
    
    List<LSFClassSet> resolveClasses();
}
