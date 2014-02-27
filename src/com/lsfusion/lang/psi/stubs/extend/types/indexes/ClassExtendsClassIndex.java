package com.lsfusion.psi.stubs.extend.types.indexes;

import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.psi.extend.LSFClassExtend;
import com.lsfusion.psi.stubs.types.LSFStubElementTypes;
import org.jetbrains.annotations.NotNull;

public class ClassExtendsClassIndex extends StringStubIndexExtension<LSFClassExtend> {

    private static final ClassExtendsClassIndex ourInstance = new ClassExtendsClassIndex();
    public static ClassExtendsClassIndex getInstance() {
        return ourInstance;
    }

    @NotNull
    @Override
    public StubIndexKey<String, LSFClassExtend> getKey() {
        return LSFStubElementTypes.EXTENDCLASS.extendKey;
    }
}
