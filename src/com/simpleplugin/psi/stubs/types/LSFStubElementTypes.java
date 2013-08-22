package com.simpleplugin.psi.stubs.types;

import com.intellij.psi.*;
import com.intellij.psi.stubs.IStubElementType;
import com.intellij.psi.stubs.StubIndexKey;
import com.simpleplugin.psi.stubs.extend.types.ExtendFormStubElementType;
import com.simpleplugin.psi.stubs.types.ClassStubElementType;

public interface LSFStubElementTypes {

    ClassStubElementType CLASS = new ClassStubElementType();
    ModuleStubElementType MODULE = new ModuleStubElementType();
    ExplicitNamespaceStubElementType EXPLICIT_NAMESPACE = new ExplicitNamespaceStubElementType();
    MetaStubElementType META = new MetaStubElementType();
    PropStubElementType PROP = new PropStubElementType();
    FormStubElementType FORM = new FormStubElementType();
    GroupStubElementType GROUP = new GroupStubElementType();
    TableStubElementType TABLE = new TableStubElementType();
    WindowStubElementType WINDOW = new WindowStubElementType();
    NavigatorElementStubElementType NAVIGATORELEMENT = new NavigatorElementStubElementType();

    ExtendFormStubElementType EXTENDFORM = new ExtendFormStubElementType();
}
