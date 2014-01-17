package com.simpleplugin.psi.stubs.types;

import com.simpleplugin.psi.stubs.extend.types.ExtendClassStubElementType;
import com.simpleplugin.psi.stubs.extend.types.ExtendFormStubElementType;

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
    ExtendClassStubElementType EXTENDCLASS = new ExtendClassStubElementType();

    ExplicitInterfaceStubElementType EXPLICIT_INTERFACE = new ExplicitInterfaceStubElementType();
}
