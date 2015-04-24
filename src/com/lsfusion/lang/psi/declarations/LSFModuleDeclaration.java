package com.lsfusion.lang.psi.declarations;

import com.lsfusion.lang.psi.references.LSFModuleReference;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import com.lsfusion.lang.psi.stubs.ModuleStubElement;

import java.util.List;

public interface LSFModuleDeclaration extends LSFNamespaceDeclaration<LSFModuleDeclaration, ModuleStubElement> {

    List<LSFModuleReference> getRequireRefs();

    // � ������� �� getRequireRefs ���������, ��� ��� ������ ��� ����� REQUIRE �������� REQUIRE System; 
    List<LSFModuleDeclaration> getRequireModules();
    
    LSFNamespaceReference getExplicitNamespaceRef();

    String getNamespace();

    List<LSFNamespaceReference> getPriorityRefs();

    boolean requires(LSFModuleDeclaration module);
}
