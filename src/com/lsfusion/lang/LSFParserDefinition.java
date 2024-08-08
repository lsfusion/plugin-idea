package com.lsfusion.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.Lexer;
import com.intellij.openapi.project.Project;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.TokenType;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.lsfusion.lang.parser.LSFParserImpl;
import com.lsfusion.lang.psi.LSFFile;
import com.lsfusion.lang.psi.LSFTypes;
import org.jetbrains.annotations.NotNull;

import static com.lsfusion.lang.psi.LSFTypes.*;

public class LSFParserDefinition implements ParserDefinition {
    public static final IStubFileElementType LSF_FILE = new IStubFileElementType("LSFLanguageStub", Language.findInstance(LSFLanguage.class));

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(LSFTypes.COMMENTS);
    public static final TokenSet STRINGS = TokenSet.create(LSFTypes.STRING_LITERAL);

    public static final TokenSet NOT_KEYWORDS = TokenSet.create(
        LSFTypes.COMMENTS,
        LEX_LOGICAL_LITERAL,
        LEX_T_LOGICAL_LITERAL,
        PRIMITIVE_TYPE,
        LEX_STRING_LITERAL,
        LEX_RAW_STRING_LITERAL,
        LEX_UINT_LITERAL,
        LEX_ULONG_LITERAL,
        LEX_UDOUBLE_LITERAL,
        LEX_UNUMERIC_LITERAL,
        LEX_DATE_LITERAL,
        LEX_DATETIME_LITERAL,
        LEX_TIME_LITERAL,
        LEX_CODE_LITERAL,
        LEX_COLOR_LITERAL,
        DOLLAR,
        EQ_OPERAND,
        LESS_EQUALS,
        LESS,
        GREATER_EQUALS,
        GREATER,
        QUESTION,
        MINUS,
        PLUS,
        MULT,
        DIV,
        ADDOR_OPERAND,
        SEMI,
        COLON,
        COMMA,
        POINT,
        EQUALS,
        PLUSEQ,
        ARROW,
        FOLLOWS,
        LBRAC,
        RBRAC,
        LBRACE,
        RBRACE,
        LSQBR,
        RSQBR,
        ATSIGN,
        ATSIGN2,
        ID
    );

    public static boolean isWhiteSpace(IElementType type) {
        return WHITE_SPACES.contains(type);
    }

    public static boolean isWhiteSpaceOrComment(IElementType type) {
        return WHITE_SPACES.contains(type) || COMMENTS.contains(type);
    }

    public static boolean isComment(IElementType type) {
        return COMMENTS.contains(type);
    }

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new LSFLexerAdapter();
    }

    @NotNull
    public TokenSet getWhitespaceTokens() {
        return WHITE_SPACES;
    }

    @NotNull
    public TokenSet getCommentTokens() {
        return COMMENTS;
    }

    @NotNull
    public TokenSet getStringLiteralElements() {
        return STRINGS;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new LSFParserImpl();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return LSF_FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new LSFFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return LSFTypes.Factory.createElement(node);
    }
}