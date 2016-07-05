package com.lsfusion.lang.psi.indexes;

import com.intellij.psi.stubs.StubIndexKey;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.extend.LSFFormExtend;

public class LSFIndexKeys {
    public static final StubIndexKey<String, LSFClassDeclaration> CLASS = StubIndexKey.createIndexKey("lsf.indices.class");
    public static final StubIndexKey<String, LSFComponentStubDeclaration> COMPONENT = StubIndexKey.createIndexKey("lsf.indices.component");
    public static final StubIndexKey<String, LSFExplicitNamespaceDeclaration> EXPLICIT_NAMESPACE = StubIndexKey.createIndexKey("lsf.indices.explicit.namespace");
    public static final StubIndexKey<String, LSFFormDeclaration> FORM = StubIndexKey.createIndexKey("lsf.indices.form");
    public static final StubIndexKey<String, LSFGroupDeclaration> GROUP = StubIndexKey.createIndexKey("lsf.indices.group");
    public static final StubIndexKey<String, LSFMetaDeclaration> META = StubIndexKey.createIndexKey("lsf.indices.meta");
    public static final StubIndexKey<String, LSFModuleDeclaration> MODULE = StubIndexKey.createIndexKey("lsf.indices.module");
    public static final StubIndexKey<String, LSFNavigatorElementDeclaration> NAVIGATORELEMENT = StubIndexKey.createIndexKey("lsf.indices.navigator.element");
    public static final StubIndexKey<String, LSFGlobalPropDeclaration> PROP = StubIndexKey.createIndexKey("lsf.indices.prop");
    public static final StubIndexKey<String, LSFTableDeclaration> TABLE = StubIndexKey.createIndexKey("lsf.indices.table");
    public static final StubIndexKey<String, LSFWindowDeclaration> WINDOW = StubIndexKey.createIndexKey("lsf.indices.window");

    public static final StubIndexKey<String, LSFTableDeclaration> TABLE_CLASSES = StubIndexKey.createIndexKey("lsf.indices.table.classes");

    public static final StubIndexKey<String, LSFFormExtend> EXTENDFORM = StubIndexKey.createIndexKey("lsf.indices.extends.form");
    public static final StubIndexKey<String, LSFClassExtend> EXTENDCLASS = StubIndexKey.createIndexKey("lsf.indices.extends.class");
    public static final StubIndexKey<String, LSFClassExtend> EXTENDCLASS_SHORT = StubIndexKey.createIndexKey("lsf.indices.extends.class.short");
    public static final StubIndexKey<String, LSFDesign> DESIGN = StubIndexKey.createIndexKey("lsf.indices.design");

    public static final StubIndexKey<String, LSFExplicitInterfacePropStatement> EXPLICIT_INTERFACE = StubIndexKey.createIndexKey("lsf.indices.explicit.interface");
    public static final StubIndexKey<String, LSFExplicitValuePropStatement> EXPLICIT_VALUE = StubIndexKey.createIndexKey("lsf.indices.explicit.value");
    public static final StubIndexKey<String, LSFImplicitInterfacePropStatement> IMPLICIT_INTERFACE = StubIndexKey.createIndexKey("lsf.indices.implicit.interface");
    public static final StubIndexKey<String, LSFImplicitValuePropStatement> IMPLICIT_VALUE = StubIndexKey.createIndexKey("lsf.indices.implicit.value");
}
