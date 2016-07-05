package com.lsfusion.lang.psi.stubs.types;

import com.lsfusion.lang.psi.stubs.extend.types.ExtendClassStubElementType;
import com.lsfusion.lang.psi.stubs.extend.types.DesignStubElementType;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendFormStubElementType;
import com.lsfusion.lang.psi.stubs.interfaces.types.ExplicitInterfaceStubElementType;
import com.lsfusion.lang.psi.stubs.interfaces.types.ExplicitValueStubElementType;
import com.lsfusion.lang.psi.stubs.interfaces.types.ImplicitInterfaceStubElementType;
import com.lsfusion.lang.psi.stubs.interfaces.types.ImplicitValueStubElementType;

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
    ComponentStubElementType COMPONENT = new ComponentStubElementType();

    ExtendFormStubElementType EXTENDFORM = new ExtendFormStubElementType();
    ExtendClassStubElementType EXTENDCLASS = new ExtendClassStubElementType();
    DesignStubElementType DESIGN = new DesignStubElementType();

    ExplicitInterfaceStubElementType EXPLICIT_INTERFACE = new ExplicitInterfaceStubElementType();
    ExplicitValueStubElementType EXPLICIT_VALUE = new ExplicitValueStubElementType();
    ImplicitValueStubElementType IMPLICIT_VALUE = new ImplicitValueStubElementType();
    ImplicitInterfaceStubElementType IMPLICIT_INTERFACE = new ImplicitInterfaceStubElementType();
}
