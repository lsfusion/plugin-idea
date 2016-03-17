package com.lsfusion.lang.psi;

import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class LSFExplicitSignature extends LSFExplicitClasses {

    public List<LSFStringClassRef> signature; // значения nullable

    public LSFExplicitSignature(List<LSFStringClassRef> signature) {
        this.signature = signature;
    }

    @NotNull
    @Override
    public Set<String> getIndexedClasses() {
        Set<String> names = new HashSet<>();
        for (LSFStringClassRef param : signature) {
            if(param != null)
                names.add(param.name);
        }
        return names;
    }

    @Override
    public void serialize(@NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(true);
        dataStream.writeInt(signature.size());
        for (LSFStringClassRef param : signature) {
            dataStream.writeBoolean(param != null);
            if(param != null)
                param.serialize(dataStream);
        }
    }

    public static LSFExplicitSignature deserialize(@NotNull StubInputStream dataStream) throws IOException {
        List<LSFStringClassRef> params = new ArrayList<>();
        int paramsCount = dataStream.readInt();
        for (int i = 0; i < paramsCount; i++) {
            params.add(dataStream.readBoolean() ? LSFStringClassRef.deserialize(dataStream) : null);//);
        }
        return new LSFExplicitSignature(params);
    }

    public boolean allClassesDeclared() {
        return LSFPsiUtils.allClassesDeclared(signature);
    }

    public LSFExplicitSignature merge(LSFExplicitSignature ex) {
        List<LSFStringClassRef> newSign = new ArrayList<>(signature);
        for(int i=0,size=newSign.size();i<size;i++) {
            LSFStringClassRef sclass = newSign.get(i);
            if (sclass == null && i < ex.signature.size())
                newSign.set(i, ex.signature.get(i));
        }
        return new LSFExplicitSignature(newSign);
    }
}
