package com.simpleplugin.psi.stubs;

import com.simpleplugin.psi.declarations.LSFModuleDeclaration;
import com.simpleplugin.psi.declarations.LSFNamespaceDeclaration;
import com.simpleplugin.psi.references.LSFModuleReference;
import com.simpleplugin.psi.references.LSFNamespaceReference;

import java.util.List;

public interface ModuleStubElement extends NamespaceStubElement<ModuleStubElement, LSFModuleDeclaration> {

    List<LSFNamespaceReference> getPriorityRefs();

    List<LSFModuleReference> getRequireRefs();

    public LSFNamespaceReference getExplicitNamespaceRef();
}
