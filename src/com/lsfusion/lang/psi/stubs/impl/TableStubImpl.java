package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.lsfusion.lang.psi.declarations.LSFTableDeclaration;
import com.lsfusion.lang.psi.stubs.TableStubElement;
import com.lsfusion.lang.psi.stubs.types.TableStubElementType;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;

public class TableStubImpl extends FullNameStubImpl<TableStubElement, LSFTableDeclaration> implements TableStubElement {
    
    private final String classNamesString;

    public TableStubImpl(StubElement parent, LSFTableDeclaration psi) {
        super(parent, psi);
        
        String[] classNames = psi.getClassNames();
        
        classNamesString = classNames.length == 0 ? "" : BaseUtils.toString(classNames);
    }

    public TableStubImpl(StubInputStream dataStream, StubElement parentStub, TableStubElementType type) throws IOException {
        super(dataStream, parentStub, type);
        classNamesString = dataStream.readUTFFast();
    }

    @NotNull
    @Override
    public String getClassNamesString() {
        return classNamesString;
    }
}
