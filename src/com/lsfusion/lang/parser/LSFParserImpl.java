package com.lsfusion.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.psi.tree.IElementType;

import static com.lsfusion.lang.parser.GeneratedParserUtilBase.GREEDY_WHITESPACE_AND_COMMENTS_PROCESSOR;
import static com.lsfusion.lang.parser.LSFParserUtil.ErrorState;
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
        //assert tokenType in script_statement_recover (recoverWhile in scriptStatement) {
        // ID | AFTER | BEFORE | HIDE | INDEX | LOGGABLE | NAVIGATOR | ON | SHOWDEP | WHEN | CONSTRAINT | INTERNAL
        // | CLASS | ATSIGN | ATSIGN2 | FORM | GROUP | META | DESIGN | TABLE | WINDOW | EXTEND | SEMI
        // to cut parsing we can use tokens (or token chains) that can be only used in top rules

        IElementType prevTokenType = tokenType;
        
        Marker lazyStatement = builder_.mark();
        builder_.advanceLexer();

        boolean isTopMetaDecl = tokenType == META;
        boolean isTopMetaUsage = tokenType == ATSIGN;

        int metaCount = isTopMetaDecl ? 1 : 0;
        int braceCount = 0;
        int tokenCount = 0;
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
                if (tokenType == DESIGN) break;
                if (tokenCount > 40) { // we don't want to have very small non-recursive lazy blocks 
                    if (tokenType == EXTEND && builder_.lookAhead(1) != FILTERGROUP) {
                        break;
                    }
                    if (tokenType == CLASS && prevTokenType != EXTEND) {
                        if (builder_.lookAhead(1) != LBRAC) { // to avoid CLASS()
                            break;
                        }
                    }
                    if(tokenType == FORM && prevTokenType != EXTEND && prevTokenType != ACTIVATE && prevTokenType != ACTIVE) {
                        // there is also usage in navigator, but there braceCount will be > 0
                        break;
                    }
                    if(tokenType == WHEN && (prevTokenType == SEMI || prevTokenType == RBRACE)) {
                        // it's not CASE and not implementation (+, +=) and not (<- WHEN) 
                        break;
                    }
                    
                    if (tokenType == INDEX) break;
                    if (tokenType == CONSTRAINT) break;
                }

                // here it's tricky, because it is not guaranteed that if there is an error, that parser will recover to the next token after SEMI (which may lead to unpredictable behaviour, but so far we didn't have any problems with that)
                if(isTopMetaUsage && builder_.lookAhead(1) == SEMI) { // @f();
                    builder_.advanceLexer(); // eat RBRACE | RBRAC
                    builder_.advanceLexer(); // eat SEMI
                    break;
                }
                if(isTopMetaDecl) { // META END
                    assert tokenType == END;
                    builder_.advanceLexer(); // eat END
                    break;
                }
            }

            prevTokenType = tokenType;
            builder_.advanceLexer();

            tokenCount++;
        }

        lazyStatement.collapse(LAZY_SCRIPT_STATEMENT);
        if (greedyBlock) {
            lazyStatement.setCustomEdgeTokenBinders(null, GREEDY_WHITESPACE_AND_COMMENTS_PROCESSOR);
        }
        return true;
    }

    public boolean lazyScriptStatementDeep(PsiBuilder builder_, int level_) {
        return super.lazyScriptStatement(builder_, level_);
    }
}
