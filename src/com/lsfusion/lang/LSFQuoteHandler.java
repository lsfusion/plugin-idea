package com.lsfusion.lang;

import com.intellij.codeInsight.editorActions.SimpleTokenSetQuoteHandler;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.highlighter.HighlighterIterator;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.util.text.CharArrayUtil;
import com.lsfusion.lang.psi.LSFTypes;

// Pairs the quote of string literals ('...' and r'...') and lets a typed quote skip over the closing one.
public class LSFQuoteHandler extends SimpleTokenSetQuoteHandler {
    public LSFQuoteHandler() {
        super(LSFTypes.LEX_STRING_LITERAL, LSFTypes.LEX_RAW_STRING_LITERAL);
    }

    @Override
    public boolean isOpeningQuote(HighlighterIterator iterator, int offset) {
        if (!isQuoteOfLiteral(iterator, offset)) {
            return false;
        }
        CharSequence chars = iterator.getDocument().getCharsSequence();
        int start = iterator.getStart();
        int quotesBefore = countQuotes(chars, start, offset);
        boolean customRawDelimiter = offset == start + 2 && isRawPrefix(chars.charAt(start));
        return quotesBefore % 2 == 0 && !customRawDelimiter;
    }

    @Override
    public boolean isClosingQuote(HighlighterIterator iterator, int offset) {
        if (super.isClosingQuote(iterator, offset)) {
            return true;
        }
        return isQuoteOfLiteral(iterator, offset) && countQuotes(iterator.getDocument().getCharsSequence(), iterator.getStart(), offset) % 2 == 1;
    }

    // SimpleTokenSetQuoteHandler.hasNonClosedLiteral with the unterminated (BAD_CHARACTER) literal counted as well
    @Override
    public boolean hasNonClosedLiteral(Editor editor, HighlighterIterator iterator, int offset) {
        int start = iterator.getStart();
        try {
            Document document = editor.getDocument();
            CharSequence chars = document.getCharsSequence();
            int lineEnd = document.getLineEndOffset(document.getLineNumber(offset));
            while (!iterator.atEnd() && iterator.getStart() < lineEnd) {
                IElementType type = iterator.getTokenType();
                if (type == TokenType.BAD_CHARACTER ? isUnterminatedLiteral(iterator, chars)
                                                    : myLiteralTokenSet.contains(type) && isNonClosedLiteral(iterator, chars)) {
                    return true;
                }
                iterator.advance();
            }
        } finally {
            while (!iterator.atEnd() && iterator.getStart() != start) {
                iterator.retreat();
            }
        }
        return false;
    }

    @Override
    protected boolean isNonClosedLiteral(HighlighterIterator iterator, CharSequence chars) {
        return super.isNonClosedLiteral(iterator, chars)
               || CharArrayUtil.indexOf(chars, "\n", iterator.getStart(), iterator.getEnd()) >= 0;
    }

    private boolean isQuoteOfLiteral(HighlighterIterator iterator, int offset) {
        IElementType type = iterator.getTokenType();
        return (myLiteralTokenSet.contains(type) || type == TokenType.BAD_CHARACTER)
               && offset < iterator.getEnd() && iterator.getDocument().getCharsSequence().charAt(offset) == '\'';
    }

    // a quote never survives as a bad character on its own: such a token is a literal the lexer could not close
    private static boolean isUnterminatedLiteral(HighlighterIterator iterator, CharSequence chars) {
        return CharArrayUtil.indexOf(chars, "'", iterator.getStart(), iterator.getEnd()) >= 0;
    }

    private static int countQuotes(CharSequence chars, int start, int end) {
        int count = 0;
        for (int i = start; i < end; i++) {
            char c = chars.charAt(i);
            if (c == '\\') {
                i++;
            } else if (c == '\'') {
                count++;
            }
        }
        return count;
    }

    private static boolean isRawPrefix(char c) {
        return c == 'R' || c == 'r';
    }
}
