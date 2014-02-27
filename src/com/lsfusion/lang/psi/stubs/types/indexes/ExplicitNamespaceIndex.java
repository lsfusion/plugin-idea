package com.lsfusion.psi.stubs.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.psi.declarations.LSFExplicitNamespaceDeclaration;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class ExplicitNamespaceIndex extends StringStubIndexExtension<LSFExplicitNamespaceDeclaration> {

    private static final ExplicitNamespaceIndex ourInstance = new ExplicitNamespaceIndex();
    public static ExplicitNamespaceIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFExplicitNamespaceDeclaration> getKey() {
        return LSFStubElementTypes.EXPLICIT_NAMESPACE.key;
    }
}
