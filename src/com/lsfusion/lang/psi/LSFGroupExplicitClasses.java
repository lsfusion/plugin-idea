package com.lsfusion.lang.psi;

import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

// группировочное свойство, но с explicit классами внутри
public class LSFGroupExplicitClasses extends LSFExplicitClasses {

    private Set<String> valueClasses;

    public LSFGroupExplicitClasses(Set<String> valueClasses) {
        this.valueClasses = valueClasses;
    }

    @NotNull
    @Override
    public Set<String> getIndexedClasses() {
        return valueClasses;
    }

    @Override
    public void serialize(@NotNull StubOutputStream dataStream) throws IOException {
        dataStream.writeBoolean(false);
        serializeSet(dataStream, valueClasses);
    }

    public static void serializeSet(@NotNull StubOutputStream dataStream, Set<String> valueClasses) throws IOException {
        dataStream.writeInt(valueClasses.size());
        for (String param : valueClasses) {
            dataStream.writeName(param);
        }
    }

    public static LSFGroupExplicitClasses deserialize(@NotNull StubInputStream dataStream) throws IOException {
        return new LSFGroupExplicitClasses(deserializeSet(dataStream));
    }

    public static Set<String> deserializeSet(@NotNull StubInputStream dataStream) throws IOException {
        int paramsCount = dataStream.readInt();
        Set<String> params = new HashSet<>();
        for (int i = 0; i < paramsCount; i++) {
            params.add(dataStream.readName().getString());
        }
        return params;
    }

}
