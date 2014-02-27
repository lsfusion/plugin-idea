package com.lsfusion.psi.stubs.types;

import com.intellij.psi.stubs.IStubElementType;

public class LSFStubElementTypeFactory {

    public static IStubElementType create(String ID) {
        if (ID.equals("NAMESPACE_NAME")) {
            return LSFStubElementTypes.EXPLICIT_NAMESPACE;
        } else if (ID.equals("MODULE_HEADER")) {
            return LSFStubElementTypes.MODULE;
        } else if (ID.equals("CLASS_DECL")) {
            return LSFStubElementTypes.CLASS;
        } else if (ID.equals("PROPERTY_STATEMENT")) {
            return LSFStubElementTypes.PROP;
        } else if (ID.equals("META_CODE_DECLARATION_STATEMENT")) {
            return LSFStubElementTypes.META;
        } else if (ID.equals("FORM_DECL")) {
            return LSFStubElementTypes.FORM;
        } else if (ID.equals("GROUP_STATEMENT")) {
            return LSFStubElementTypes.GROUP;
        } else if (ID.equals("TABLE_STATEMENT")) {
            return LSFStubElementTypes.TABLE;
        } else if (ID.equals("WINDOW_CREATE_STATEMENT")) {
            return LSFStubElementTypes.WINDOW;
        } else if (ID.equals("NEW_NAVIGATOR_ELEMENT_STATEMENT")) {
            return LSFStubElementTypes.NAVIGATORELEMENT;
        } else if (ID.equals("FORM_STATEMENT")) {
            return LSFStubElementTypes.EXTENDFORM;
        } else if (ID.equals("CLASS_STATEMENT")) {
            return LSFStubElementTypes.EXTENDCLASS;
        } else if (ID.equals("EXPLICIT_INTERFACE_PROPERTY_STATEMENT")) {
            return LSFStubElementTypes.EXPLICIT_INTERFACE;
        } else if (ID.equals("EXPLICIT_VALUE_PROPERTY_STATEMENT")) {
            return LSFStubElementTypes.EXPLICIT_VALUE;
        } else if (ID.equals("IMPLICIT_VALUE_PROPERTY_STATEMENT")) {
            return LSFStubElementTypes.IMPLICIT_VALUE;
        } else if (ID.equals("IMPLICIT_INTERFACE_PROPERTY_STATEMENT")) {
            return LSFStubElementTypes.IMPLICIT_INTERFACE;
        }

        throw new UnsupportedOperationException();
    }

}
