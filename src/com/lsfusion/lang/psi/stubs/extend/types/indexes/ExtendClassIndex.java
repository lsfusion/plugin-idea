package com.lsfusion.psi.stubs.extend.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.psi.extend.LSFClassExtend;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class ExtendClassIndex extends StringStubIndexExtension<LSFClassExtend> {

    private static final ExtendClassIndex ourInstance = new ExtendClassIndex();
    public static ExtendClassIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFClassExtend> getKey() {
        return LSFStubElementTypes.EXTENDCLASS.key;
    }
}
