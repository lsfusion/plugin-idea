package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFFormDeclaration;
import org.jetbrains.annotations.NotNull;

public class FormIndex extends LSFStringStubIndex<LSFFormDeclaration> {

    private static final FormIndex ourInstance = new FormIndex();

    public static FormIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFFormDeclaration> getKey() {
        return LSFIndexKeys.FORM;
    }

    @Override
    protected Class<LSFFormDeclaration> getPsiClass() {
        return LSFFormDeclaration.class;
    }
}
