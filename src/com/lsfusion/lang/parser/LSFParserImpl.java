package com.lsfusion.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilder.Marker;
import com.intellij.psi.tree.IElementType;

import static com.lsfusion.lang.parser.GeneratedParserUtilBase.GREEDY_WHITESPACE_AND_COMMENTS_PROCESSOR;
import static com.lsfusion.lang.parser.LSFParserUtil.ErrorState;
import static com.lsfusion.lang.psi.LSFTypes.*;

public class LSFParserImpl extends LSFParser {

    @Override
    public boolean parse_root_(IElementType root_, PsiBuilder builder_) {
        Boolean deepParse = parseLazyDeep(root_, builder_, 0);
        if(deepParse != null)
            return deepParse;

        return super.parse_root_(root_, builder_);
    }

    private Boolean parseLazyDeep(IElementType root_, PsiBuilder builder_, int level_) {
        if (root_ == LAZY_SCRIPT_STATEMENT) {
            return lazyScriptStatementDeep(builder_, level_);
        } else if (root_ == LAZY_META_DECL_STATEMENT) {
            return lazyMetaDeclStatementDeep(builder_, level_);
        } else if (root_ == LAZY_META_STATEMENT) {
            return lazyMetaStatementDeep(builder_, level_);
        }
        return null;
    }

    public boolean lazyScriptStatement(PsiBuilder builder_, int level_) {
        return lazyStatement(builder_, true, null, LAZY_SCRIPT_STATEMENT, level_);
    }
    public boolean lazyMetaDeclStatement(PsiBuilder builder_, int level_) {
        return lazyStatement(builder_, false, END, LAZY_META_DECL_STATEMENT, level_);
    }
    public boolean lazyMetaStatement(PsiBuilder builder_, int level_) {
        return lazyStatement(builder_, false, RBRACE, LAZY_META_STATEMENT, level_);
    }
    public boolean lazyStatement(PsiBuilder builder_, boolean containMeta, IElementType tokenEnd, IElementType statementType, int level_) {
        ErrorState state = ErrorState.get(builder_);
        if (state.completionCallback != null) {
            return parseLazyDeep(statementType, builder_, level_);
        }

        IElementType tokenType = builder_.getTokenType();
        if (tokenType == null) {
            return false;
        }
        if (tokenType == tokenEnd) {
            return false;
        }
        //assert tokenType in script_statement_recover (recoverWhile in scriptStatement) {
        // ID | AFTER | BEFORE | HIDE | INDEX | NAVIGATOR | ON | SHOWDEP | WHEN | CONSTRAINT | INTERNAL
        // | CLASS | ATSIGN | ATSIGN2 | FORM | GROUP | META | DESIGN | TABLE | WINDOW | EXTEND | SEMI
        // to cut parsing we can use tokens (or token chains) that can be only used in top rules

        IElementType prevTokenType = tokenType;
        
        Marker lazyStatement = builder_.mark();
        builder_.advanceLexer();

        boolean isTopMetaDecl = containMeta && tokenType == META;
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

            if (braceCount == 0 && metaCount == 0) {
                if(tokenType == tokenEnd)
                    break;
                if(containMeta && tokenType == META)
                    break;
            }

            if (tokenType == LBRACE) {
                braceCount++;
            } else if (tokenType == RBRACE) {
                if (braceCount > 0) {
                    braceCount--;
                }
            } else if(containMeta) {
                if (tokenType == META) {
                    metaCount++;
                } else if (checEndTokenType(tokenType, prevTokenType)) {
                    if (metaCount > 0) {
                        metaCount--;
                    }
                }
            }

            if (braceCount == 0 && metaCount == 0) {
                if (tokenType == NAVIGATOR) break;
                if (tokenType == ATSIGN) break;
                if (tokenType == DESIGN) break;
                if (tokenType == WINDOW && prevTokenType == SEMI) break; //to avoid WINDOW ... CLASS (in windowOptions)
                if (tokenCount > 40) { // we don't want to have very small non-recursive lazy blocks
                    IElementType nextTokenType = builder_.lookAhead(1);
                    if (tokenType == EXTEND && nextTokenType != OBJECTS && nextTokenType != PROPERTIES && nextTokenType != FILTERGROUP) {
                        break;
                    }
                    if (tokenType == CLASS && prevTokenType != EXTEND) {
                        if (nextTokenType != LBRAC) { // to avoid CLASS()
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
                    assert checEndTokenType(tokenType, prevTokenType);
                    builder_.advanceLexer(); // eat END
                    break;
                }
            }

            prevTokenType = tokenType;
            builder_.advanceLexer();

            tokenCount++;
        }

        lazyStatement.collapse(statementType);
        if (greedyBlock) {
            lazyStatement.setCustomEdgeTokenBinders(null, GREEDY_WHITESPACE_AND_COMMENTS_PROCESSOR);
        }
        return true;
    }

    private boolean checEndTokenType(IElementType tokenType, IElementType prevTokenType) {
        //check prevTokenType because it may be alignmentLiteral / flexAlignmentLiteral ('align = END')
        return tokenType == END && prevTokenType != EQUALS;
    }

    public boolean lazyScriptStatementDeep(PsiBuilder builder_, int level_) {
        return super.lazyScriptStatement(builder_, level_);
    }
    public boolean lazyMetaDeclStatementDeep(PsiBuilder builder_, int level_) {
        return super.lazyMetaDeclStatement(builder_, level_);
    }
    public boolean lazyMetaStatementDeep(PsiBuilder builder_, int level_) {
        return super.lazyMetaStatement(builder_, level_);
    }
}
