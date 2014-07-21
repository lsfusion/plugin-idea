package com.lsfusion.lang.psi.indexes;

import com.intellij.util.io.DataInputOutputUtil;
import com.intellij.util.io.IOUtil;
import com.intellij.util.io.KeyDescriptor;
import org.jetbrains.annotations.NotNull;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;
import java.util.Arrays;

public class StringArrayIndexKeyDescriptor implements KeyDescriptor<String[]> {
    public static final StringArrayIndexKeyDescriptor INSTANCE = new StringArrayIndexKeyDescriptor();
    
    @Override
    public int getHashCode(final String[] value) {
        
        return Arrays.hashCode(value);
    }

    @Override
    public boolean isEqual(final String[] val1, final String[] val2) {
        return Arrays.equals(val1, val2);
    }

    @Override
    public void save(@NotNull final DataOutput out, @NotNull final String[] value) throws IOException {
        DataInputOutputUtil.writeINT(out, value.length);
//        out.writeInt(value.length);
        
        final byte[] buffer = IOUtil.allocReadWriteUTFBuffer();
        for (String element : value) {
            IOUtil.writeUTFFast(buffer, out, element);
        }
    }

    @Override
    public String[] read(@NotNull final DataInput in) throws IOException {
        final byte[] buffer = IOUtil.allocReadWriteUTFBuffer();

        int len = DataInputOutputUtil.readINT(in);
//        int len = in.readInt();
        String[] value = new String[len];
        for (int i = 0; i < len; i++) {
            value[i] = IOUtil.readUTFFast(buffer, in);
        }
        
        return value;
    }
}
