package com.lsfusion.lang.psi;

import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LSFExplicitSignature extends LSFExplicitClasses {

    public List<LSFStringClassRef> signature;

    public LSFExplicitSignature(List<LSFStringClassRef> signature) {
        this.signature = signature;
    }

    @NotNull
    @Override
    public Set<String> getIndexedClasses() {
        Set<String> names = new HashSet<>();
        for (LSFStringClassRef param : signature) {
            names.add(param.name);
        }
        return names;
    }

    @Override
    public void serialize(@NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(true);
        dataStream.writeInt(signature.size());
        for (LSFStringClassRef param : signature) {
            param.serialize(dataStream);
        }
    }

    public static LSFExplicitSignature deserialize(@NotNull StubInputStream dataStream) throws IOException {
        List<LSFStringClassRef> params = new ArrayList<LSFStringClassRef>();
        int paramsCount = dataStream.readInt();
        for (int i = 0; i < paramsCount; i++) {
            params.add(LSFStringClassRef.deserialize(dataStream));//);
        }
        return new LSFExplicitSignature(params);
    }
}
