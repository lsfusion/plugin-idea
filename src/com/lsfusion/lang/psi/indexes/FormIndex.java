package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import org.jetbrains.annotations.NotNull;

public class FormIndex extends StringStubIndexExtension<LSFFormDeclaration> {

    private static final FormIndex ourInstance = new FormIndex();

    public static FormIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFFormDeclaration> getKey() {
        return LSFIndexKeys.FORM;
    }
}