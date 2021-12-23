package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.LSFActionDeclaration;
import org.jetbrains.annotations.NotNull;

public class ActionIndex extends ActionOrPropIndex<LSFActionDeclaration> {

    private static final ActionIndex ourInstance = new ActionIndex();

    public static ActionIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFActionDeclaration> getKey() {
        return LSFIndexKeys.ACTION;
    }

    @Override
    protected Class<LSFActionDeclaration> getPsiClass() {
        return LSFActionDeclaration.class;
    }
}
