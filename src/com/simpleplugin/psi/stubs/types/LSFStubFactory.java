package com.simpleplugin.psi.stubs.types;

public class LSFStubFactory {

    public static GlobalStubElementType create(String ID) {
        if(ID.equals("NAMESPACE_NAME"))
            return LSFStubElementTypes.EXPLICIT_NAMESPACE;
        if(ID.equals("MODULE_HEADER"))
            return LSFStubElementTypes.MODULE;
        if(ID.equals("CLASS_STATEMENT"))
            return LSFStubElementTypes.CLASS;
        if(ID.equals("PROPERTY_STATEMENT"))
            return LSFStubElementTypes.PROP;
        if(ID.equals("META_CODE_DECLARATION_STATEMENT"))
            return LSFStubElementTypes.META;
        throw new UnsupportedOperationException();
    }

}
