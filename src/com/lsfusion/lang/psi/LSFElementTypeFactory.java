
package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.psi.impl.LSFLazyScriptStatementImpl;
import org.jetbrains.annotations.NotNull;

public class LSFElementTypeFactory {
    public static final IElementType LAZY_SCRIPT_STATEMENT = new LSFLazyParsableElementType("LAZY_SCRIPT_STATEMENT") {
        @NotNull
        @Override
        public ASTNode createNode(CharSequence text) {
            return new LSFLazyScriptStatementImpl(this, text);
        }
    };

    public static IElementType create(String ID) {
        if (ID.equals("LAZY_SCRIPT_STATEMENT")) {
            return LAZY_SCRIPT_STATEMENT;
        }

        return new LSFElementType(ID);
    }
}