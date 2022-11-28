package com.lsfusion.lang.psi.stubs.types;

import com.lsfusion.lang.psi.stubs.extend.types.ExtendClassStubElementType;
import com.lsfusion.lang.psi.stubs.extend.types.DesignStubElementType;
import com.lsfusion.lang.psi.stubs.extend.types.ExtendFormStubElementType;
import com.lsfusion.lang.psi.stubs.interfaces.types.*;

public interface LSFStubElementTypes {

    ClassStubElementType CLASS = new ClassStubElementType();
    ModuleStubElementType MODULE = new ModuleStubElementType();
    ExplicitNamespaceStubElementType EXPLICIT_NAMESPACE = new ExplicitNamespaceStubElementType();
    MetaStubElementType META = new MetaStubElementType();
    StatementPropStubElementType STATEMENTPROP = new StatementPropStubElementType();
    AggrParamPropStubElementType AGGRPARAMPROP = new AggrParamPropStubElementType();
    BaseEventActionStubElementType BASEEVENTPE = new BaseEventActionStubElementType();
    ActionStubElementType ACTION = new ActionStubElementType();
    FormStubElementType FORM = new FormStubElementType();
    GroupStubElementType GROUP = new GroupStubElementType();
    TableStubElementType TABLE = new TableStubElementType();
    WindowStubElementType WINDOW = new WindowStubElementType();
    NavigatorElementStubElementType NAVIGATORELEMENT = new NavigatorElementStubElementType();

    ExtendFormStubElementType EXTENDFORM = new ExtendFormStubElementType();
    ExtendClassStubElementType EXTENDCLASS = new ExtendClassStubElementType();
    DesignStubElementType DESIGN = new DesignStubElementType();

    ExplicitInterfacePropStubElementType EXPLICIT_INTERFACE_PROP = new ExplicitInterfacePropStubElementType();
    ExplicitValueStubElementType EXPLICIT_VALUE = new ExplicitValueStubElementType();
    ImplicitValueStubElementType IMPLICIT_VALUE = new ImplicitValueStubElementType();
    ImplicitInterfaceStubElementType IMPLICIT_INTERFACE = new ImplicitInterfaceStubElementType();

    ExplicitInterfaceActionStubElementType EXPLICIT_INTERFACE_ACTION = new ExplicitInterfaceActionStubElementType();
}
