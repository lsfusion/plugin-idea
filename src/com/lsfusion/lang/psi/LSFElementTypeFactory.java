
package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.psi.impl.LSFLazyMetaDeclStatementImpl;
import com.lsfusion.lang.psi.impl.LSFLazyMetaStatementImpl;
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
    public static final IElementType LAZY_META_DECL_STATEMENT= new LSFLazyParsableElementType("LAZY_META_DECL_STATEMENT") {
        @NotNull
        @Override
        public ASTNode createNode(CharSequence text) {
            return new LSFLazyMetaDeclStatementImpl(this, text);
        }
    };
    public static final IElementType LAZY_META_STATEMENT= new LSFLazyParsableElementType("LAZY_META_STATEMENT") {
        @NotNull
        @Override
        public ASTNode createNode(CharSequence text) {
            return new LSFLazyMetaStatementImpl(this, text);
        }
    };

    public static IElementType create(String ID) {
        switch (ID) {
            case "LAZY_SCRIPT_STATEMENT":
                return LAZY_SCRIPT_STATEMENT;
            case "LAZY_META_DECL_STATEMENT":
                return LAZY_META_DECL_STATEMENT;
            case "LAZY_META_STATEMENT":
                return LAZY_META_STATEMENT;
            default:
                return new LSFElementType(ID);
        }
    }
}