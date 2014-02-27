package com.lsfusion;

import com.intellij.lexer.FlexAdapter;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.JBColor;
import com.lsfusion.psi.LSFTypes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class LSFSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey SEPARATOR = createTextAttributesKey("SIMPLE_SEPARATOR", DefaultLanguageHighlighterColors.OPERATION_SIGN);
    public static final TextAttributesKey KEY = createTextAttributesKey("SIMPLE_KEY", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey VALUE = createTextAttributesKey("SIMPLE_VALUE", DefaultLanguageHighlighterColors.STRING);
    public static final TextAttributesKey NUMBER = createTextAttributesKey("SIMPLE_NUMBER", DefaultLanguageHighlighterColors.NUMBER);
    public static final TextAttributesKey COMMENT = createTextAttributesKey("SIMPLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SIMPLE_BAD_CHARACTER",
            new TextAttributes(JBColor.RED, null, null, null, Font.BOLD));

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] SEPARATOR_KEYS = new TextAttributesKey[]{SEPARATOR};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] VALUE_KEYS = new TextAttributesKey[]{VALUE};
    private static final TextAttributesKey[] NUMBER_KEYS = new TextAttributesKey[]{NUMBER};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new FlexAdapter(new LSFLexer((Reader) null));
    }

    Set<IElementType> operands = new HashSet<IElementType>(Arrays.asList(new IElementType[] {LSFTypes.RBRAC, LSFTypes.EQ_OPERAND, LSFTypes.LESS, LSFTypes.GREATER, LSFTypes.LESS_EQUALS, LSFTypes.GREATER_EQUALS, LSFTypes.MINUS, 
                        LSFTypes.PLUS, LSFTypes.MULT_OPERAND, LSFTypes.ADDOR_OPERAND, LSFTypes.SEMI, LSFTypes.COLON, LSFTypes.COMMA, LSFTypes.POINT, LSFTypes.EQUALS, LSFTypes.PLUSEQ, LSFTypes.ARROW, LSFTypes.FOLLOWS, LSFTypes.LBRAC, LSFTypes.RBRAC, LSFTypes.LBRACE, LSFTypes.RBRACE, LSFTypes.LSQBR, LSFTypes.RSQBR, LSFTypes.ATSIGN}));
           
    Set<IElementType> nonStringLiterals = new HashSet<IElementType>(Arrays.asList(new IElementType[] {LSFTypes.LEX_UINT_LITERAL, 
            LSFTypes.LEX_ULONG_LITERAL, LSFTypes.LEX_UDOUBLE_LITERAL, LSFTypes.LEX_UNUMERIC_LITERAL, LSFTypes.LEX_DATE_LITERAL, LSFTypes.LEX_DATETIME_LITERAL, LSFTypes.LEX_TIME_LITERAL, LSFTypes.LEX_COLOR_LITERAL}));

@NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(LSFTypes.ID))
            return EMPTY_KEYS;
        
        if (operands.contains(tokenType))
            return EMPTY_KEYS;

        if (tokenType.equals(LSFTypes.LEX_STRING_LITERAL))
            return VALUE_KEYS;

        if (nonStringLiterals.contains(tokenType))
            return NUMBER_KEYS;
            
        if (tokenType.equals(LSFTypes.COMMENTS))
            return COMMENT_KEYS;
    
        if (tokenType.equals(TokenType.BAD_CHARACTER))
            return BAD_CHAR_KEYS;
    
        return KEY_KEYS;
    }
}