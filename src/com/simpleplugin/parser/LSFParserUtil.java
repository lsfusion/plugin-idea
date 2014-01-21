package com.simpleplugin.parser;

import com.intellij.lang.PsiBuilder;
import com.intellij.lang.parser.GeneratedParserUtilBase;
import com.intellij.openapi.util.Key;
import com.simpleplugin.psi.LSFTypes;

@SuppressWarnings("StringEquality")
public class LSFParserUtil extends GeneratedParserUtilBase {

    public static boolean readAny(PsiBuilder builder_, int level_) {
        if(nextTokenIs(builder_, LSFTypes.END))
            return false;

        builder_.advanceLexer();
        return true;
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
        if(builder_.getTokenType() == LSFTypes.ID && 
                builder_.lookAhead(1) == LSFTypes.POINT && 
                builder_.lookAhead(2) == LSFTypes.ID &&
                builder_.lookAhead(3) != LSFTypes.POINT) {
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
}