package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.psi.declarations.LSFFullNameDeclaration;
import com.lsfusion.lang.psi.stubs.FullNameStubElement;

import java.io.IOException;

public class FullNameStubImpl<This extends FullNameStubElement<This, Decl>, Decl extends LSFFullNameDeclaration<Decl, This>> extends GlobalStubImpl<This, Decl> implements FullNameStubElement<This, Decl> {
    
    protected int offset;
    
    public FullNameStubImpl(StubElement parent, Decl psi) {
        super(parent, psi);
        
        offset = psi.getOffset();
    }

    public FullNameStubImpl(StubInputStream dataStream, StubElement parentStub, IStubElementType<This, Decl> type) throws IOException {
        super(dataStream, parentStub, type);
        
        offset = dataStream.readInt();
    }

    public FullNameStubImpl(StubElement parent, IStubElementType elementType, StringRef name, int offset) {
        super(parent, elementType, name);
        
        this.offset = offset;
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);
        
        dataStream.writeInt(offset);        
    }

    @Override
    public int getOffset() {
        return offset;
    }
}
