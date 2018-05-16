package com.lsfusion.lang.psi.stubs.types;

import com.intellij.psi.stubs.IStubElementType;

public class LSFStubElementTypeFactory {

    public static IStubElementType create(String ID) {
        switch (ID) {
            case "NAMESPACE_NAME":
                return LSFStubElementTypes.EXPLICIT_NAMESPACE;
            case "MODULE_HEADER":
                return LSFStubElementTypes.MODULE;
            case "CLASS_DECL":
                return LSFStubElementTypes.CLASS;
            case "PROPERTY_STATEMENT":
                return LSFStubElementTypes.STATEMENTPROP;
            case "AGGR_PARAM_PROP_DECLARE":
                return LSFStubElementTypes.AGGRPARAMPROP;
            case "ACTION_STATEMENT":
                return LSFStubElementTypes.ACTION;
            case "META_CODE_DECLARATION_STATEMENT":
                return LSFStubElementTypes.META;
            case "FORM_DECL":
                return LSFStubElementTypes.FORM;
            case "GROUP_STATEMENT":
                return LSFStubElementTypes.GROUP;
            case "TABLE_STATEMENT":
                return LSFStubElementTypes.TABLE;
            case "WINDOW_CREATE_STATEMENT":
                return LSFStubElementTypes.WINDOW;
            case "NEW_NAVIGATOR_ELEMENT_STATEMENT":
                return LSFStubElementTypes.NAVIGATORELEMENT;
            case "FORM_STATEMENT":
                return LSFStubElementTypes.EXTENDFORM;
            case "DESIGN_STATEMENT":
                return LSFStubElementTypes.DESIGN;
            case "CLASS_STATEMENT":
                return LSFStubElementTypes.EXTENDCLASS;
            case "EXPLICIT_INTERFACE_PROPERTY_STATEMENT":
                return LSFStubElementTypes.EXPLICIT_INTERFACE_PROP;
            case "EXPLICIT_VALUE_PROPERTY_STATEMENT":
                return LSFStubElementTypes.EXPLICIT_VALUE;
            case "IMPLICIT_VALUE_PROPERTY_STATEMENT":
                return LSFStubElementTypes.IMPLICIT_VALUE;
            case "IMPLICIT_INTERFACE_PROPERTY_STATEMENT":
                return LSFStubElementTypes.IMPLICIT_INTERFACE;
            case "COMPONENT_STUB_DECL":
                return LSFStubElementTypes.COMPONENT;
            case "EXPLICIT_INTERFACE_ACT_STATEMENT":
                return LSFStubElementTypes.EXPLICIT_INTERFACE_ACTION;
        }

        throw new UnsupportedOperationException();
    }

}
