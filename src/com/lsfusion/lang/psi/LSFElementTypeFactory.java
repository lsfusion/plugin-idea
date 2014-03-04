
package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.LSFLexerAdapter;
import com.lsfusion.lang.psi.impl.LSFComponentBlockStatementImpl;
import org.jetbrains.annotations.NotNull;

public class LSFElementTypeFactory {
    public static final IElementType COMPONENT_BLOCK_STATEMENT = new LSFReparsableElementType("COMPONENT_STATEMENT_BODY") {
        @NotNull
        @Override
        public ASTNode createNode(CharSequence text) {
            return new LSFComponentBlockStatementImpl(this, text);
        }

        @Override
        public int getErrorsCount(CharSequence seq, Language fileLanguage, Project project) {
            final Lexer lexer = new LSFLexerAdapter();

            lexer.start(seq);
            if (lexer.getTokenType() != LSFTypes.LBRACE) return FATAL_ERROR;
            lexer.advance();
            int balance = 1;
            while (true) {
                IElementType type = lexer.getTokenType();
                if (type == null) break;
                if (balance == 0) return FATAL_ERROR;
                if (type == LSFTypes.LBRACE) {
                    balance++;
                }
                else if (type == LSFTypes.RBRACE) {
                    balance--;
                }
                lexer.advance();
            }
            return balance;
        }
    };

    public static final IElementType SCRIPT_STATEMENT = new LSFElementType("SCRIPT_STATEMENT");
//    public static final IElementType SCRIPT_STATEMENT = new LSFReparsableElementType("SCRIPT_STATEMENT") {
//        @NotNull
//        @Override
//        public ASTNode createNode(CharSequence text) {
//            return new LSFScriptStatementImpl(this, text);
//        }
//
//        @Override
//        public int getErrorsCount(CharSequence seq, Language fileLanguage, Project project) {
//          return FATAL_ERROR;
//        }
//    };

    public static IElementType create(String ID) {
        if (ID.equals("COMPONENT_BLOCK_STATEMENT")) {
            return COMPONENT_BLOCK_STATEMENT;
        } else if (ID.equals("SCRIPT_STATEMENT")) {
            return SCRIPT_STATEMENT;
        }

        return new LSFElementType(ID);
    }
}