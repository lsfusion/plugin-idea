
package com.lsfusion.migration.lang;

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
import com.intellij.psi.tree.IFileElementType;
import com.intellij.psi.tree.IStubFileElementType;
import com.intellij.psi.tree.TokenSet;
import com.lsfusion.migration.MigrationLexerAdapter;
import com.lsfusion.migration.lang.parser.MigrationParser;
import com.lsfusion.migration.lang.psi.MigrationFile;
import com.lsfusion.migration.lang.psi.MigrationTypes;
import org.jetbrains.annotations.NotNull;

import static com.lsfusion.migration.lang.psi.MigrationTypes.*;

public class MigrationParserDefinition implements ParserDefinition {
    public static final IStubFileElementType FILE = new IStubFileElementType(Language.findInstance(MigrationLanguage.class));

    public static final TokenSet WHITE_SPACES = TokenSet.create(TokenType.WHITE_SPACE);
    public static final TokenSet COMMENTS = TokenSet.create(MigrationTypes.COMMENTS);

    public static final TokenSet NOT_KEYWORDS = TokenSet.create(
            MigrationTypes.COMMENTS,
            PRIMITIVE_TYPE,
            POINT,
            ARROW,
            LBRACE,
            RBRACE,
            LSQBR,
            RSQBR,
            LBRACKET,
            RBRACKET,
            UNKNOWNCLASS,
            ID
    );

    @NotNull
    @Override
    public Lexer createLexer(Project project) {
        return new MigrationLexerAdapter();
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
        return TokenSet.EMPTY;
    }

    @NotNull
    public PsiParser createParser(final Project project) {
        return new MigrationParser();
    }

    @Override
    public IFileElementType getFileNodeType() {
        return FILE;
    }

    public PsiFile createFile(FileViewProvider viewProvider) {
        return new MigrationFile(viewProvider);
    }

    public SpaceRequirements spaceExistanceTypeBetweenTokens(ASTNode left, ASTNode right) {
        return SpaceRequirements.MAY;
    }

    @NotNull
    public PsiElement createElement(ASTNode node) {
        return Factory.createElement(node);
    }
}