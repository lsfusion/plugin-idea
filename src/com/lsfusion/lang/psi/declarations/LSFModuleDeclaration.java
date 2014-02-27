package com.lsfusion.psi.declarations;

import com.lsfusion.psi.references.LSFModuleReference;
import com.lsfusion.psi.references.LSFNamespaceReference;
import com.lsfusion.psi.stubs.ModuleStubElement;

import java.util.List;

public interface LSFModuleDeclaration extends LSFNamespaceDeclaration<LSFModuleDeclaration, ModuleStubElement> {

    List<LSFModuleReference> getRequireRefs();

    LSFNamespaceReference getExplicitNamespaceRef();

    String getNamespace();

    List<LSFNamespaceReference> getPriorityRefs();

}
