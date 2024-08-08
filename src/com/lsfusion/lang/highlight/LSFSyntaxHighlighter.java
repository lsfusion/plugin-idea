package com.lsfusion.lang.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.JBColor;
import com.lsfusion.lang.LSFLexerAdapter;
import com.lsfusion.lang.psi.LSFTypes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class LSFSyntaxHighlighter extends SyntaxHighlighterBase {
    private static final TextAttributesKey KEYWORD = createTextAttributesKey("LSF_KEYWORD", DefaultLanguageHighlighterColors.KEYWORD);
    private static final TextAttributesKey STRING_LITERAL = createTextAttributesKey("LSF_STRING_LITERAL", DefaultLanguageHighlighterColors.STRING);
    private static final TextAttributesKey RAW_STRING_LITERAL = createTextAttributesKey("LSF_RAW_STRING_LITERAL", DefaultLanguageHighlighterColors.STRING);
    
    private static final TextAttributesKey NUMBER_LITERAL = createTextAttributesKey("LSF_LITERAL", DefaultLanguageHighlighterColors.NUMBER);
    private static final TextAttributesKey COMMENT = createTextAttributesKey("LSF_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);
    private static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("LSF_BAD_CHARACTER", new TextAttributes(JBColor.RED, null, null, null, Font.BOLD));

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] KEYWORD_KEYS = new TextAttributesKey[]{KEYWORD};
    private static final TextAttributesKey[] STR_LITERAL_KEYS = new TextAttributesKey[]{STRING_LITERAL};
    private static final TextAttributesKey[] RAW_STR_LITERAL_KEYS = new TextAttributesKey[]{RAW_STRING_LITERAL};
    private static final TextAttributesKey[] NUMBER_LITERAL_KEYS = new TextAttributesKey[]{NUMBER_LITERAL};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new LSFLexerAdapter();
    }

    private Set<IElementType> operands = new HashSet<>(Arrays.asList(LSFTypes.RBRAC, LSFTypes.EQ_OPERAND, LSFTypes.LESS, 
            LSFTypes.GREATER, LSFTypes.LESS_EQUALS, LSFTypes.GREATER_EQUALS, LSFTypes.MINUS,
            LSFTypes.PLUS, LSFTypes.MULT, LSFTypes.DIV, LSFTypes.ADDOR_OPERAND, LSFTypes.SEMI, LSFTypes.COLON, 
            LSFTypes.COMMA, LSFTypes.POINT, LSFTypes.EQUALS, LSFTypes.PLUSEQ, LSFTypes.ARROW, LSFTypes.FOLLOWS, LSFTypes.LBRAC, 
            LSFTypes.RBRAC, LSFTypes.LBRACE, LSFTypes.RBRACE, LSFTypes.LSQBR, LSFTypes.RSQBR, LSFTypes.ATSIGN, LSFTypes.ATSIGN2));
           
    private Set<IElementType> nonStringLiterals = new HashSet<>(Arrays.asList(LSFTypes.LEX_UINT_LITERAL,
            LSFTypes.LEX_ULONG_LITERAL, LSFTypes.LEX_UDOUBLE_LITERAL, LSFTypes.LEX_UNUMERIC_LITERAL, LSFTypes.LEX_DATE_LITERAL, 
            LSFTypes.LEX_DATETIME_LITERAL, LSFTypes.LEX_TIME_LITERAL, LSFTypes.LEX_COLOR_LITERAL));

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(LSFTypes.ID))
            return EMPTY_KEYS;
        
        if (operands.contains(tokenType))
            return EMPTY_KEYS;

        if (tokenType.equals(LSFTypes.LEX_STRING_LITERAL))
            return STR_LITERAL_KEYS;

        if (tokenType.equals(LSFTypes.LEX_RAW_STRING_LITERAL))
            return RAW_STR_LITERAL_KEYS;
 
        if (nonStringLiterals.contains(tokenType))
            return NUMBER_LITERAL_KEYS;
            
        if (tokenType.equals(LSFTypes.COMMENTS))
            return COMMENT_KEYS;
    
        if (tokenType.equals(TokenType.BAD_CHARACTER))
            return BAD_CHAR_KEYS;
    
        return KEYWORD_KEYS;
    }
}