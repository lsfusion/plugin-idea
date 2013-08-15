package com.simpleplugin.psi.declarations;

import com.simpleplugin.psi.LSFRequireList;
import com.simpleplugin.psi.references.LSFModuleReference;
import com.simpleplugin.psi.references.LSFNamespaceReference;
import com.simpleplugin.psi.stubs.ModuleStubElement;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public interface LSFModuleDeclaration extends LSFNamespaceDeclaration<LSFModuleDeclaration, ModuleStubElement> {

    List<LSFModuleReference> getRequireRefs();

    LSFNamespaceReference getExplicitNamespaceRef();

    String getNamespace();

    List<LSFNamespaceReference> getPriorityRefs();

}
