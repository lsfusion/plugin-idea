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
            case "BASE_EVENT_PE":
            case "BASE_EVENT_NOT_PE":
                return LSFStubElementTypes.BASEEVENTACTION;
            case "ACTION_STATEMENT":
                return LSFStubElementTypes.STATEMENTACTION;
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
            case "OVERRIDE_ACTION_STATEMENT":
                return LSFStubElementTypes.OVERRIDEACTION;
            case "OVERRIDE_PROPERTY_STATEMENT":
                return LSFStubElementTypes.OVERRIDEPROPERTY;
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
            case "EXPLICIT_INTERFACE_ACT_STATEMENT":
                return LSFStubElementTypes.EXPLICIT_INTERFACE_ACTION;
        }

        throw new UnsupportedOperationException();
    }

}
