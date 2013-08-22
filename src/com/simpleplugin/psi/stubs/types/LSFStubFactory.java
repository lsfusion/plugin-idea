package com.simpleplugin.psi.stubs.types;

import com.intellij.psi.tree.IElementType;

public class LSFStubFactory {

    public static GlobalStubElementType create(String ID) {
        if(ID.equals("NAMESPACE_NAME"))
            return LSFStubElementTypes.EXPLICIT_NAMESPACE;
        if(ID.equals("MODULE_HEADER"))
            return LSFStubElementTypes.MODULE;
        if(ID.equals("CLASS_DECL"))
            return LSFStubElementTypes.CLASS;
        if(ID.equals("PROPERTY_STATEMENT"))
            return LSFStubElementTypes.PROP;
        if(ID.equals("META_CODE_DECLARATION_STATEMENT"))
            return LSFStubElementTypes.META;
        if(ID.equals("FORM_DECL"))
            return LSFStubElementTypes.FORM;
        if(ID.equals("GROUP_STATEMENT"))
            return LSFStubElementTypes.GROUP;
        if(ID.equals("TABLE_STATEMENT"))
            return LSFStubElementTypes.TABLE;
        if(ID.equals("WINDOW_CREATE_STATEMENT"))
            return LSFStubElementTypes.WINDOW;
        if(ID.equals("NEW_NAVIGATOR_ELEMENT_STATEMENT"))
            return LSFStubElementTypes.NAVIGATORELEMENT;
        if(ID.equals("FORM_STATEMENT"))
            return LSFStubElementTypes.EXTENDFORM;
        
        throw new UnsupportedOperationException();
    }

}
