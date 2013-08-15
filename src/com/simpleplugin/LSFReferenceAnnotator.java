package com.simpleplugin;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.DefaultLanguageHighlighterColors;
import com.intellij.openapi.editor.colors.CodeInsightColors;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.references.LSFGlobalReference;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LSFReferenceAnnotator extends LSFVisitor implements Annotator {
    private AnnotationHolder myHolder;

    @Override
    public synchronized void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        myHolder = holder;
        try {
            psiElement.accept(this);
        } finally {
            myHolder = null;
        }
    }

    private final static TextAttributes META_USAGE = new TextAttributes(null, Gray._239, null, EffectType.BOXED, Font.PLAIN);
    private final static TextAttributes META_DECL = new TextAttributes(null, new JBColor(new Color(255, 255, 192), new Color(0, 0, 64)), null, EffectType.BOXED, Font.PLAIN);
    private final static TextAttributes ERROR = new TextAttributes(new JBColor(new Color(255, 0, 0), new Color(0, 255, 255)), null, null, EffectType.BOXED, Font.PLAIN);

    @Override
    public void visitElement(@NotNull PsiElement o) {
        if(o instanceof LeafPsiElement && isInMetaUsage(o)) { // фокус в том что побеждает наибольший приоритет, но важно следить что у верхнего правила всегда приоритет выше, так как в противном случае annotator просто херится
            Annotation annotation = myHolder.createInfoAnnotation(o.getTextRange(), null);
            annotation.setEnforcedTextAttributes(META_USAGE);
        }
    }

    @Override
    public void visitPropertyUsage(@NotNull LSFPropertyUsage o) {
        super.visitPropertyUsage(o);
        checkReference(o);
    }

    @Override
    public void visitCustomClassUsage(@NotNull LSFCustomClassUsage o) {
        super.visitCustomClassUsage(o);
        checkReference(o);
    }

    @Override
    public void visitFormUsage(@NotNull LSFFormUsage o) {
        super.visitFormUsage(o);
        
        checkReference(o);
    }

    @Override
    public void visitTableUsage(@NotNull LSFTableUsage o) {
        super.visitTableUsage(o);
        
        checkReference(o);
    }

    @Override
    public void visitGroupUsage(@NotNull LSFGroupUsage o) {
        super.visitGroupUsage(o);
        
        checkReference(o);
    }

    @Override
    public void visitWindowUsage(@NotNull LSFWindowUsage o) {
        super.visitWindowUsage(o);
        
        checkReference(o);
    }

    @Override
    public void visitNavigatorElementUsage(@NotNull LSFNavigatorElementUsage o) {
        super.visitNavigatorElementUsage(o);
        
        checkReference(o);
    }

    private boolean isInMetaUsage(PsiElement o) {
        return PsiTreeUtil.getParentOfType(o, LSFMetaCodeBody.class) != null;
        //&& PsiTreeUtil.getParentOfType(o, LSFMetaCodeStatement.class) == null
    }

    private boolean isInMetaDecl(PsiElement o) {
        return PsiTreeUtil.getParentOfType(o, LSFMetaCodeDeclarationStatement.class) != null;
        //&& PsiTreeUtil.getParentOfType(o, LSFMetaCodeStatement.class) == null
    }
    @Override
    public void visitMetaCodeStatement(@NotNull LSFMetaCodeStatement o) {
        super.visitMetaCodeStatement(o);

        checkReference(o);
    }

    private void checkReference(LSFGlobalReference reference) {
        if (!isInMetaDecl(reference) && !reference.isSoft() && reference.resolve() == null)
            addError(reference);
    }

    public void visitMetaCodeDeclarationStatement(@NotNull LSFMetaCodeDeclarationStatement o) {
        super.visitMetaCodeDeclarationStatement(o);

        LSFAnyTokens statements = o.getAnyTokens();
        if(statements!=null) {
            Annotation annotation = myHolder.createInfoAnnotation(statements.getTextRange(), "");
            annotation.setEnforcedTextAttributes(META_DECL);
        }
    }

    private void addError(LSFGlobalReference reference) {
        final Annotation annotation = myHolder.createErrorAnnotation(reference.getTextRange(), "Cannot resolve symbol");
        TextAttributes error = ERROR;
        if(isInMetaUsage(reference))
            error = TextAttributes.merge(error, META_USAGE);
        annotation.setEnforcedTextAttributes(error);
    }
}

