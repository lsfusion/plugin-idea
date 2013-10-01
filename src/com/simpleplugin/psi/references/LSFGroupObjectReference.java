package com.simpleplugin.psi.references;

import com.simpleplugin.classes.LSFClassSet;
import com.simpleplugin.psi.declarations.LSFGroupObjectDeclaration;

import java.util.List;

public interface LSFGroupObjectReference extends LSFFormElementReference<LSFGroupObjectDeclaration> {
    
    List<LSFClassSet> resolveClasses();
}
