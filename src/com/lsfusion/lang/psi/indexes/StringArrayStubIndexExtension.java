package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.PsiElement;
import com.intellij.psi.stubs.AbstractStubIndex;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

public abstract class StringArrayStubIndexExtension<Psi extends PsiElement> extends AbstractStubIndex<String[], Psi> {
    @Override
    public int getVersion() {
        return 0;
    }

    @NotNull
    @Override
    public KeyDescriptor<String[]> getKeyDescriptor() {
        return StringArrayIndexKeyDescriptor.INSTANCE;
    }
}
