package com.lsfusion.migration.lang.highlight;

import com.intellij.lexer.Lexer;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.fileTypes.SyntaxHighlighterBase;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.ui.JBColor;
import com.lsfusion.migration.MigrationLexerAdapter;
import com.lsfusion.migration.lang.psi.MigrationTypes;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import static com.intellij.openapi.editor.colors.TextAttributesKey.createTextAttributesKey;

public class MigrationSyntaxHighlighter extends SyntaxHighlighterBase {
    public static final TextAttributesKey KEY = createTextAttributesKey("SIMPLE_KEY", DefaultLanguageHighlighterColors.KEYWORD);
    public static final TextAttributesKey COMMENT = createTextAttributesKey("SIMPLE_COMMENT", DefaultLanguageHighlighterColors.LINE_COMMENT);

    static final TextAttributesKey BAD_CHARACTER = createTextAttributesKey("SIMPLE_BAD_CHARACTER",
                                                                           new TextAttributes(JBColor.RED, null, null, null, Font.BOLD));

    private static final TextAttributesKey[] BAD_CHAR_KEYS = new TextAttributesKey[]{BAD_CHARACTER};
    private static final TextAttributesKey[] KEY_KEYS = new TextAttributesKey[]{KEY};
    private static final TextAttributesKey[] COMMENT_KEYS = new TextAttributesKey[]{COMMENT};
    private static final TextAttributesKey[] EMPTY_KEYS = new TextAttributesKey[0];

    static Set<IElementType> OPERAND_TYPES = new HashSet<IElementType>(
            Arrays.asList(
                    new IElementType[]{
                            MigrationTypes.POINT,
                            MigrationTypes.ARROW,
                            MigrationTypes.LBRACE, MigrationTypes.RBRACE,
                            MigrationTypes.LSQBR, MigrationTypes.RSQBR,
                            MigrationTypes.LBRACKET, MigrationTypes.RBRACKET
                    }
            )
    );

    @NotNull
    @Override
    public Lexer getHighlightingLexer() {
        return new MigrationLexerAdapter();
    }

    @NotNull
    @Override
    public TextAttributesKey[] getTokenHighlights(IElementType tokenType) {
        if (tokenType.equals(MigrationTypes.ID)) {
            return EMPTY_KEYS;
        }

        if (OPERAND_TYPES.contains(tokenType)) {
            return EMPTY_KEYS;
        }

        if (tokenType.equals(MigrationTypes.COMMENTS)) {
            return COMMENT_KEYS;
        }

        if (tokenType.equals(TokenType.BAD_CHARACTER)) {
            return BAD_CHAR_KEYS;
        }

        return KEY_KEYS;
    }
}