package com.lsfusion.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.psi.tree.IElementType;

import static com.intellij.lang.java.parser.JavaParserUtil.GREEDY_RIGHT_EDGE_PROCESSOR;
import static com.lsfusion.lang.parser.LSFParserUtil.*;
import static com.lsfusion.lang.psi.LSFTypes.*;

public class LSFParserImpl extends LSFParser {

    @Override
    public boolean parseImpl(IElementType root_, PsiBuilder builder_) {
        if (root_ == LAZY_SCRIPT_STATEMENT) {
            return lazyScriptStatementDeep(builder_, 0);
        } else {
            return super.parseImpl(root_, builder_);
        }
    }

    public boolean lazyScriptStatement(PsiBuilder builder_, int level_) {
        ErrorState state = ErrorState.get(builder_);
        if (state.completionCallback != null) {
            return lazyScriptStatementDeep(builder_, level_);
        }

        IElementType tokenType = builder_.getTokenType();
        if (tokenType == null) {
            return false;
        }
        //assert tokenType in {
        // ID | AFTER | BEFORE | HIDE | INDEX | LOGGABLE | NAVIGATOR | ON | SHOWDEP | WHEN | CONSTRAINT
        //    | CLASS | ATSIGN | FORM | GROUP | META | DESIGN | TABLE | WINDOW | EXTEND | SEMI
        // },
        // because of recoverWhile in moduleHeader

        Marker lazyStatement = builder_.mark();
        builder_.advanceLexer();
        
        boolean isExtend = tokenType == EXTEND;
        int metaCount = tokenType == META ? 1 : 0;
        int braceCount = 0;
        boolean greedyBlock = false;
        while (true) {
            tokenType = builder_.getTokenType();
            if (tokenType == null) {
                greedyBlock = true;
                break;
            }

            if (tokenType == LBRACE) {
                braceCount++;
            } else if (tokenType == RBRACE) {
                if (braceCount > 0) {
                    braceCount--;
                }
            } else if (tokenType == META) {
                if (braceCount == 0 && metaCount == 0) break;
                metaCount++;
            } else if (tokenType == END) {
                if (metaCount > 0) {
                    metaCount--;
                }
            }

            if (braceCount == 0 && metaCount == 0) {
                if (tokenType == NAVIGATOR) break;
                if (tokenType == ATSIGN) break;
                if (tokenType == META) break;
                if (tokenType == EXTEND) break;
                if (!isExtend && tokenType == DESIGN) break;

                // не выделяем мелкие statement'ы в отдельные куски, т.к. это только тормозит парсинг
//              if (tokenType == INDEX) break;
//              if (tokenType == CONSTRAINT) break;
//              if (tokenType == HIDE && builder_.lookAhead(1) == WINDOW) break;
//              if (tokenType == WINDOW) break;
            }

            builder_.advanceLexer();
        }

        lazyStatement.collapse(LAZY_SCRIPT_STATEMENT);
        if (greedyBlock) {
            lazyStatement.setCustomEdgeTokenBinders(null, GREEDY_RIGHT_EDGE_PROCESSOR);
        }
        return true;
    }

    public boolean lazyScriptStatementDeep(PsiBuilder builder_, int level_) {
        return super.lazyScriptStatement(builder_, level_);
    }
}
