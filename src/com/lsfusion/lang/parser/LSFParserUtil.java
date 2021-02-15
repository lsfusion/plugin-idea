package com.lsfusion.lang.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.impl.PsiBuilderAdapter;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.openapi.util.Key;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.lang.psi.LSFTypes;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("StringEquality")
public class LSFParserUtil extends GeneratedParserUtilBase {

    public static boolean readAny(PsiBuilder builder_, int level_) {
        if (builder_.eof() || nextTokenIs(builder_, LSFTypes.END)) {
            return false;
        }

        builder_.advanceLexer();
        return true;
    }

    public static boolean notSimpleIdAhead(PsiBuilder builder_, int level_) {
        return builder_.getTokenType() == LSFTypes.ID && builder_.lookAhead(1) == LSFTypes.LBRAC;
    }

    private static Key<Boolean> INNERID = Key.create("lsf.inner.id");

    public static boolean innerIDStop(PsiBuilder builder_, int level_) {
        Boolean userData = builder_.getUserData(INNERID);
        if (userData != null && userData) {
            builder_.putUserData(INNERID, false);
            return false;
        }
        return true;
    }

    public static boolean innerIDCheck(PsiBuilder builder_, int level_) {
        boolean predictNamespace = builder_.getTokenType() == LSFTypes.ID &&
                                   builder_.lookAhead(1) == LSFTypes.POINT &&
                                   builder_.lookAhead(2) == LSFTypes.ID &&
                                   builder_.lookAhead(3) == LSFTypes.POINT;

        if (!predictNamespace) {
            builder_.putUserData(INNERID, true);
        }
        return true;
    }

    public static boolean pivotCheck(PsiBuilder builder_, int level_) {
        IElementType token = builder_.getTokenType();
        return  token != LSFTypes.GROUP_OBJECT_USAGE &&
                token != LSFTypes.COLUMNS &&
                token != LSFTypes.ROWS &&
                token != LSFTypes.MEASURES;
    }

    private static Key<Integer> MATCHEDDRAWOBJECT = Key.create("lsf.matched.draw.object");

    public static boolean checkFormExpr(PsiBuilder builder_, int level_) {
        builder_.putUserData(MATCHEDDRAWOBJECT, null);
        return true;
    }
    public static boolean matchedFormDrawObject(PsiBuilder builder_, int level_) {
        builder_.putUserData(MATCHEDDRAWOBJECT, builder_.rawTokenIndex());
        return true;
    }

    public static boolean matchedFormExpr(PsiBuilder builder_, int level_) {
        Integer userData = builder_.getUserData(MATCHEDDRAWOBJECT);
        if (userData != null && userData == builder_.rawTokenIndex()) {
            builder_.putUserData(MATCHEDDRAWOBJECT, null); // just in case
            return false;
        }        
        return true;
    }

    private static Key<Integer> MATCHEDSINGLEPARAMETER = Key.create("lsf.matched.single.parameter");

    public static boolean checParameterOrExpression(PsiBuilder builder_, int level_) {
        builder_.putUserData(MATCHEDSINGLEPARAMETER, null);
        return true;
    }
    public static boolean matchedSingleParameter(PsiBuilder builder_, int level_) {
        builder_.putUserData(MATCHEDSINGLEPARAMETER, builder_.rawTokenIndex());
        return true;
    }

    public static boolean matchedParameterOrExpression(PsiBuilder builder_, int level_) {
        Integer userData = builder_.getUserData(MATCHEDSINGLEPARAMETER);
        if (userData != null && userData == builder_.rawTokenIndex()) {
            builder_.putUserData(MATCHEDSINGLEPARAMETER, null); // just in case
            return false;
        }        
        return true;
    }

    public static boolean noIDCheck(PsiBuilder builder_, int level_) {
        return (builder_.getTokenType() != LSFTypes.ID);
    }

    private static Key<Boolean> FULLCOMPOUND = Key.create("lsf.full.compound.param.declare");

    public static boolean fullCompoundParamDeclareStop(PsiBuilder builder_, int level_) {
        Boolean userData = builder_.getUserData(FULLCOMPOUND);
        if (userData != null && userData) {
            return false;
        }
        return true;
    }

    public static boolean fullCompoundParamDeclareCheck(PsiBuilder builder_, int level_) {
        if (builder_.getTokenType() == LSFTypes.ID &&
            builder_.lookAhead(1) == LSFTypes.POINT &&
            builder_.lookAhead(2) == LSFTypes.ID &&
            builder_.lookAhead(3) == LSFTypes.ID) {
            builder_.putUserData(FULLCOMPOUND, true);
        } else {
            builder_.putUserData(FULLCOMPOUND, false);
        }
        return true;
    }

    public static boolean semicolonIfNeeded(PsiBuilder builder_, int level) {
        IElementType prevToken = lookBehind(builder_, 1);
        if (prevToken != LSFTypes.RBRACE && prevToken != LSFTypes.SEMI) {
            if (builder_.getTokenType() == LSFTypes.SEMI) {
                builder_.advanceLexer();
                return true;
            }
            builder_.error("semicolon expected");
            return false;
        }
        return true;
    }
//
//    public static boolean tempSemicolonIfNeeded(PsiBuilder builder_, int level) {
//        IElementType prevToken = lookBehind(builder_, 1);
//        if (prevToken != LSFTypes.RBRACE && prevToken != LSFTypes.SEMI) {
//            if (builder_.getTokenType() == LSFTypes.SEMI) {
//                builder_.advanceLexer();
//                return true;
//            }
////            builder_.error("semicolon expected");
//            return true;
//        }
//        return true;
//    }

    @Nullable
    public static IElementType lookBehind(PsiBuilder builder_, int steps) {
        PsiBuilderImpl builder = (PsiBuilderImpl) (builder_ instanceof PsiBuilderImpl ? builder_ : ((PsiBuilderAdapter) builder_).getDelegate());

        int currIndex = 0;
        IElementType curr;
        do {
            curr = builder.rawLookup(--currIndex);
            while (curr != null && builder.whitespaceOrComment(curr)) {
                curr = builder.rawLookup(--currIndex);
            }
            --steps;
        } while (steps > 0 && curr != null);

        return steps == 0 ? curr : null;
    }

    private static Key<Integer> PARSING_MODE_KEY = Key.create("lsf.parsing.mode");
    private static final int MODE_EXPRESSION = 1;
    private static final int MODE_ACTION = 2;
    public static void setExpressionParsing(PsiBuilder builder_) {
        builder_.putUserData(PARSING_MODE_KEY, MODE_EXPRESSION);
    }
    
    public static void setActionParsing(PsiBuilder builder_) {
        builder_.putUserData(PARSING_MODE_KEY, MODE_ACTION);
    }
    
    public static boolean isExpressionParsing(PsiBuilder builder_, int level) {
        Integer value = builder_.getUserData(PARSING_MODE_KEY);
        return value != null && value == MODE_EXPRESSION;
    }
    
    public static boolean isActionParsing(PsiBuilder builder_, int level) {
        Integer value = builder_.getUserData(PARSING_MODE_KEY);
        return value != null && value == MODE_ACTION;
    }
    
}