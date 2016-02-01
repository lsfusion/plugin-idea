package com.lsfusion.lang.psi;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceStubElement;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public abstract class LSFExplicitClasses {

    @NotNull
    public abstract Set<String> getIndexedClasses();

    public abstract void serialize(@NotNull StubOutputStream dataStream) throws IOException;

    public static LSFExplicitClasses deserialize(@NotNull StubInputStream dataStream) throws IOException {
        if(dataStream.readBoolean()) {
            return LSFExplicitSignature.deserialize(dataStream);
        } else {
            return LSFGroupExplicitClasses.deserialize(dataStream);
        }
    }

}
