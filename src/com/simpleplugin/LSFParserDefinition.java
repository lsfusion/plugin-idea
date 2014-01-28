
package com.simpleplugin;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.ParserDefinition;
import com.intellij.lang.PsiParser;
import com.intellij.lexer.FlexAdapter;
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
import com.simpleplugin.parser.LSFParser;
import com.simpleplugin.psi.LSFFile;
import com.simpleplugin.psi.LSFTypes;
import org.jetbrains.annotations.NotNull;

import java.io.Reader;

public class LSFParserDefinition implements ParserDefinition{
    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(LSFTypes.COMMENTS);
    public static final TokenSet STRINGS = TokenSet.create(LSFTypes.STRING_LITERAL);

    public static boolean isWhiteSpace(IElementType type) {
        return WHITE_SPACES.contains(type);
    }
    public static boolean isWhiteSpaceOrComment(IElementType type) {
        return WHITE_SPACES.contains(type) || COMMENTS.contains(type);
    }

    public static final IStubFileElementType FILE = new IStubFileElementType(Language.<LSFLanguage>findInstance(LSFLanguage.class));
    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new FlexAdapter(new LSFLexer((Reader) null));
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
        return new LSFParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
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