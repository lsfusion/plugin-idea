package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFExplicitNamespaceDeclaration;
import org.jetbrains.annotations.NotNull;

public class ExplicitNamespaceIndex extends LSFStringStubIndex<LSFExplicitNamespaceDeclaration> {

    private static final ExplicitNamespaceIndex ourInstance = new ExplicitNamespaceIndex();

    public static ExplicitNamespaceIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFExplicitNamespaceDeclaration> getKey() {
        return LSFIndexKeys.EXPLICIT_NAMESPACE;
    }

    @Override
    protected Class<LSFExplicitNamespaceDeclaration> getPsiClass() {
        return LSFExplicitNamespaceDeclaration.class;
    }
}
