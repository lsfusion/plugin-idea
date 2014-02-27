package com.lsfusion.lang.psi.stubs.extend.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class ExtendFormIndex extends StringStubIndexExtension<LSFFormExtend> {

    private static final ExtendFormIndex ourInstance = new ExtendFormIndex();
    public static ExtendFormIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFFormExtend> getKey() {
        return LSFStubElementTypes.EXTENDFORM.key;
    }
}

