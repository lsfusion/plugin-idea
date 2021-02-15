package com.lsfusion.lang.psi.stubs.interfaces.types;

import com.intellij.psi.stubs.*;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.LSFExplicitClasses;
import com.lsfusion.lang.psi.declarations.LSFExplicitInterfaceActionOrPropStatement;
import com.lsfusion.lang.psi.stubs.interfaces.ExplicitInterfaceActionOrPropStubElement;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementType;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public abstract class ExplicitInterfaceActionOrPropStubElementType<Stub extends ExplicitInterfaceActionOrPropStubElement<Stub, Psi>, Psi extends LSFExplicitInterfaceActionOrPropStatement<Psi, Stub>> extends LSFStubElementType<Stub, Psi> {
    public ExplicitInterfaceActionOrPropStubElementType(@NotNull String debugName) {
        super(debugName);
    }

    protected abstract StubIndexKey getIndexKey();
    
    @Override
    public void indexStub(@NotNull Stub stub, @NotNull IndexSink sink) {
        LSFExplicitClasses paramExplicitClasses = stub.getParamExplicitClasses();
        if(paramExplicitClasses != null) {
            Set<String> set = new HashSet<>(); // избегаем повторного добавления при многократном вхождении класса
            for (String paramClass : paramExplicitClasses.getIndexedClasses()) {
                if (paramClass != null && set.add(paramClass))
                    sink.occurrence(getIndexKey(), paramClass);
            }
        }
    }

}
