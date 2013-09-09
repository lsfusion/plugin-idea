package com.simpleplugin.psi.stubs.extend.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.simpleplugin.psi.extend.LSFClassExtend;
import com.simpleplugin.psi.stubs.extend.ExtendClassStubElement;
import com.simpleplugin.psi.stubs.extend.ExtendStubElement;
import com.simpleplugin.psi.stubs.extend.types.ExtendClassStubElementType;
import com.simpleplugin.psi.stubs.extend.types.ExtendStubElementType;
import org.apache.commons.lang.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExtendClassStubImpl extends ExtendStubImpl<LSFClassExtend, ExtendClassStubElement> implements ExtendClassStubElement {

    private List<StringRef> shortExtends;

    @Override
    public List<String> getShortExtends() {
        List<String> result = new ArrayList<String>();
        for(StringRef shortExtend : shortExtends)
            result.add(StringRef.toString(shortExtend));
        return result;
    }

    public ExtendClassStubImpl(StubElement parent, LSFClassExtend psi) {
        super(parent, psi);

        shortExtends = new ArrayList<StringRef>();
        for(String shortExtend : psi.getShortExtends())
            shortExtends.add(StringRef.fromString(shortExtend));
    }

    public ExtendClassStubImpl(StubInputStream dataStream, StubElement parentStub, ExtendClassStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
        
        shortExtends = new ArrayList<StringRef>();
        for(int i=0,size=dataStream.readInt();i<size;i++)
            shortExtends.add(dataStream.readName());
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);
        
        dataStream.writeInt(shortExtends.size());
        for (StringRef shortExtend : shortExtends) 
            dataStream.writeName(StringRef.toString(shortExtend));
    }
}
