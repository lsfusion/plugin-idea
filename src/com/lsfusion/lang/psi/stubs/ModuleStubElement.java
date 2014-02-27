package com.lsfusion.psi.stubs;

import com.lsfusion.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.psi.references.LSFModuleReference;
import com.lsfusion.psi.references.LSFNamespaceReference;

import java.util.List;

public interface ModuleStubElement extends NamespaceStubElement<ModuleStubElement, LSFModuleDeclaration> {

    List<LSFNamespaceReference> getPriorityRefs();

    List<LSFModuleReference> getRequireRefs();

    public LSFNamespaceReference getExplicitNamespaceRef();
}
