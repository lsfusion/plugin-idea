package com.lsfusion.lang.psi;

import com.intellij.lang.ASTNode;
import com.intellij.lang.PsiBuilder;
import com.intellij.lang.PsiBuilderFactory;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.ICodeFragmentElementType;
import com.intellij.psi.tree.IFileElementType;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.parser.LSFParserImpl;
import com.lsfusion.lang.parser.LSFParserUtil;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.NotNull;

public abstract class LSFCodeFragmentElementType extends ICodeFragmentElementType {

    public static final IFileElementType LSF_EXPRESSION_FRAGMENT = new LSFCodeFragmentElementType("LSF_EXPRESSION_FRAGMENT") {
        @Override
        protected void setupPsiBuilder(PsiBuilder builder) {
            LSFParserUtil.setExpressionParsing(builder);
        }
    };
    
    public static final IFileElementType LSF_ACTION_FRAGMENT = new LSFCodeFragmentElementType("LSF_ACTION_FRAGMENT") {
        @Override
        protected void setupPsiBuilder(PsiBuilder builder) {
            LSFParserUtil.setActionParsing(builder);
        }
    };

    public LSFCodeFragmentElementType(@NonNls String debugName) {
        super(debugName, LSFLanguage.INSTANCE);
    }

    @Override
    protected ASTNode doParseContents(@NotNull ASTNode chameleon, @NotNull PsiElement psi) {
        final PsiBuilder builder = PsiBuilderFactory.getInstance()
            .createBuilder(psi.getProject(), chameleon, null, LSFLanguage.INSTANCE, chameleon.getChars());

        setupPsiBuilder(builder);

        return new LSFParserImpl().parse(this, builder).getFirstChildNode();
    }

    protected abstract void setupPsiBuilder(PsiBuilder builder);
}
