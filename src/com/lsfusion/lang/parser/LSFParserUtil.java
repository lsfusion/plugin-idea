package com.lsfusion.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.impl.PsiBuilderAdapter;
import com.intellij.lang.impl.PsiBuilderImpl;
import com.intellij.openapi.util.Key;
import com.intellij.psi.tree.IElementType;
import com.lsfusion.psi.LSFTypes;
import org.jetbrains.annotations.Nullable;

@SuppressWarnings("StringEquality")
public class LSFParserUtil extends GeneratedParserUtilBase {

    public static boolean readAny(PsiBuilder builder_, int level_) {
        if(builder_.eof() || nextTokenIs(builder_, LSFTypes.END))
            return false;

        builder_.advanceLexer();
        return true;
    }

    public static boolean notSimpleIdAhead(PsiBuilder builder_, int level_) {
        return builder_.getTokenType() == LSFTypes.ID
               && (builder_.lookAhead(1) == LSFTypes.LBRAC || builder_.lookAhead(1) == LSFTypes.POINT);
    }

    private static Key<Boolean> INNERID = Key.create("lsf.inner.id");

    public static boolean innerIDStop(PsiBuilder builder_, int level_) {
        Boolean userData = builder_.getUserData(INNERID);
        if(userData != null && userData) {
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

        if(!predictNamespace) {
            builder_.putUserData(INNERID, true);
        }
        return true;
    }

    private static Key<Boolean> FULLCOMPOUND = Key.create("lsf.full.compound.param.declare");

    public static boolean fullCompoundParamDeclareStop(PsiBuilder builder_, int level_) {
        Boolean userData = builder_.getUserData(FULLCOMPOUND);
        if(userData != null && userData) {
            return false;
        }
        return true;
    }

    public static boolean fullCompoundParamDeclareCheck(PsiBuilder builder_, int level_) {
        if(builder_.getTokenType() == LSFTypes.ID &&
                builder_.lookAhead(1) == LSFTypes.POINT &&
                builder_.lookAhead(2) == LSFTypes.ID &&
                builder_.lookAhead(3) == LSFTypes.ID) {
            builder_.putUserData(FULLCOMPOUND, true);
        } else
            builder_.putUserData(FULLCOMPOUND, false);
        return true;
    }

    public static boolean semicolonIfNeeded(PsiBuilder builder_, int level) {
        if (lookBehind(builder_, 1) != LSFTypes.RBRACE) {
            if (builder_.getTokenType() == LSFTypes.SEMI) {
                builder_.advanceLexer();
                return true;
            }
            return false;
        }
        return true;
    }

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
}