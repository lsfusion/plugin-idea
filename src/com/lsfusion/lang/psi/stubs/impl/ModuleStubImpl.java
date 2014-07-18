package com.lsfusion.lang.psi.stubs.impl;

import com.intellij.psi.stubs.StubElement;
import com.intellij.psi.stubs.StubInputStream;
import com.intellij.psi.stubs.StubOutputStream;
import com.intellij.util.io.StringRef;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.declarations.LSFModuleDeclaration;
import com.lsfusion.lang.psi.references.LSFModuleReference;
import com.lsfusion.lang.psi.references.LSFNamespaceReference;
import com.lsfusion.lang.psi.stubs.ModuleStubElement;
import com.lsfusion.lang.psi.stubs.types.ModuleStubElementType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ModuleStubImpl extends NamespaceStubImpl<ModuleStubElement, LSFModuleDeclaration> implements ModuleStubElement {

    private final List<StringRef> requires;
    private final StringRef namespace;
    private final List<StringRef> priorities;

    public ModuleStubImpl(StubElement parent, LSFModuleDeclaration psi) {
        super(parent, psi);

        LSFNamespaceReference explicit = psi.getExplicitNamespaceRef();
        namespace = explicit == null ? null : StringRef.fromString(explicit.getNameRef());

        requires = new ArrayList<StringRef>();
        for (LSFModuleReference ref : psi.getRequireRefs()) {
            requires.add(StringRef.fromString(ref.getNameRef()));
        }

        priorities = new ArrayList<StringRef>();
        for (LSFNamespaceReference ref : psi.getPriorityRefs()) {
            priorities.add(StringRef.fromString(ref.getNameRef()));
        }
    }

    public LSFFile getFile() {
        return getPsi().getLSFFile();
    }

    public List<LSFModuleReference> getRequireRefs() {
        LSFFile file = getFile();

        List<LSFModuleReference> result = new ArrayList<LSFModuleReference>();
        for (StringRef req : requires) {
            result.add(LSFElementGenerator.createModuleRefFromText(req, file));
        }
        return result;
    }

    public List<LSFNamespaceReference> getPriorityRefs() {
        LSFFile file = getFile();

        List<LSFNamespaceReference> result = new ArrayList<LSFNamespaceReference>();
        for (StringRef req : requires) {
            result.add(LSFElementGenerator.createNamespaceRefFromText(req, file));
        }
        return result;
    }

    public LSFNamespaceReference getExplicitNamespaceRef() {
        if (namespace == null) {
            return null;
        }

        return LSFElementGenerator.createNamespaceRefFromText(namespace, getFile());
    }

    @Override
    public void serialize(StubOutputStream dataStream) throws IOException {
        super.serialize(dataStream);

        dataStream.writeName(StringRef.toString(namespace));

        dataStream.writeInt(requires.size());
        for (StringRef require : requires) {
            dataStream.writeName(StringRef.toString(require));
        }

        dataStream.writeInt(priorities.size());
        for (StringRef priority : priorities) {
            dataStream.writeName(StringRef.toString(priority));
        }
    }

    public ModuleStubImpl(StubInputStream dataStream, StubElement parentStub, ModuleStubElementType type) throws IOException {
        super(dataStream, parentStub, type);

        namespace = dataStream.readName();

        requires = new ArrayList<StringRef>();
        for (int i = 0, size = dataStream.readInt(); i < size; i++) {
            requires.add(dataStream.readName());
        }

        priorities = new ArrayList<StringRef>();
        for (int i = 0, size = dataStream.readInt(); i < size; i++) {
            priorities.add(dataStream.readName());
        }
    }
}
