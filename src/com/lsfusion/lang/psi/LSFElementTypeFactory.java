
package com.lsfusion.lang.psi;

import com.intellij.psi.tree.IElementType;

public class LSFElementTypeFactory {
    public static final IElementType COMPONENT_STATEMENT_BODY = new LSFElementType("COMPONENT_STATEMENT_BODY");

    public static IElementType create(String ID) {
        if (ID.equals("COMPONENT_STATEMENT_BODY")) {
            return COMPONENT_STATEMENT_BODY;
        }

        return new LSFElementType(ID);
    }
}