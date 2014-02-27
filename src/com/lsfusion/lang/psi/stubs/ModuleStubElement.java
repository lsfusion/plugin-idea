package com.lsfusion.lang.psi.stubs;

import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFModuleReference;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;

import java.util.List;

public interface ModuleStubElement extends NamespaceStubElement<ModuleStubElement, LSFModuleDeclaration> {

    List<LSFNamespaceReference> getPriorityRefs();

    List<LSFModuleReference> getRequireRefs();

    public LSFNamespaceReference getExplicitNamespaceRef();
}
