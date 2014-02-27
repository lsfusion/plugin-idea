package com.lsfusion.psi.references;

import com.lsfusion.classes.LSFClassSet;
import com.lsfusion.psi.declarations.LSFGroupObjectDeclaration;

import java.util.List;

public interface LSFGroupObjectReference extends LSFFormElementReference<LSFGroupObjectDeclaration> {
    
    List<LSFClassSet> resolveClasses();
}
