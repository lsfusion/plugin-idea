package com.simpleplugin;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.psi.PsiElement;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.simpleplugin.psi.*;
import com.simpleplugin.psi.references.LSFPropReference;
import com.simpleplugin.psi.references.LSFReference;
import org.jetbrains.annotations.NotNull;

import java.awt.*;

public class LSFReferenceAnnotator extends LSFVisitor implements Annotator {
    private AnnotationHolder myHolder;
    public boolean errorsSearchMode = false;

    @Override
    public synchronized void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder) {
        myHolder = holder;
        try {
            psiElement.accept(this);
        } finally {
            myHolder = null;
            errorsSearchMode = false;
        }
    }
    
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder holder, boolean errorsSearchMode) {
        this.errorsSearchMode = errorsSearchMode;
        annotate(psiElement, holder);
    }

    private final static TextAttributes META_USAGE = new TextAttributes(null, new JBColor(Gray._239, Gray._61), null, null, Font.PLAIN);
    private final static TextAttributes META_DECL = new TextAttributes(null, new JBColor(new Color(255, 255, 192), new Color(37, 49, 37)), null, null, Font.PLAIN);
    private final static TextAttributes ERROR = new TextAttributes(new JBColor(new Color(255, 0, 0), new Color(188, 63, 60)), null, null, null, Font.PLAIN);
    public final static TextAttributes WAVE_UNDERSCORED_ERROR = new TextAttributes(null, null, new JBColor(new Color(255, 0, 0), new Color(188, 63, 60)), EffectType.WAVE_UNDERSCORE, Font.PLAIN);
    private final static TextAttributes IMPLICIT_DECL = new TextAttributes(Gray._96, null, null, null, Font.PLAIN);

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
        if(checkReference(o) && !o.isDirect())
            addIndirectProp(o);
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

/*    @Override
    public void visitModifyParamContext(@NotNull ModifyParamContext o) {
        super.visitModifyParamContext(o);
        
        if(!(o instanceof ExtendParamContext) && PsiTreeUtil.getParentOfType(o, ModifyParamContext.class) == null) {
            Annotation annotation = myHolder.createWarningAnnotation(o.getTextRange(), "Infer type");
            annotation.registerFix(new TypeInferAction(o));
        }
    }
*/
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

    @Override
    public void visitExprParameterNameUsage(@NotNull LSFExprParameterNameUsage o) {
        super.visitExprParameterNameUsage(o);
        
        checkReference(o);
        
        if(o.resolveDecl()==o.getClassParamDeclare().getParamDeclare())
            addImplicitDecl(o);
    }

    @Override
    public void visitObjectUsage(@NotNull LSFObjectUsage o) {
        super.visitObjectUsage(o);

        checkReference(o);
    }

    public static boolean isInMetaUsage(PsiElement o) {
        return PsiTreeUtil.getParentOfType(o, LSFMetaCodeBody.class) != null;
        //&& PsiTreeUtil.getParentOfType(o, LSFMetaCodeStatement.class) == null
    }
/*
    @Override
    public void visitMetaDeclaration(@NotNull LSFMetaDeclaration o) {
        super.visitMetaDeclaration(o);

        Annotation annotation = myHolder.createWarningAnnotation(o.getTextRange(), "Infer type");
        annotation.registerFix(new MetaTypeInferAction(o));
    }*/

    private boolean isInMetaDecl(PsiElement o) {
        return PsiTreeUtil.getParentOfType(o, LSFMetaCodeDeclarationStatement.class) != null;
        //&& PsiTreeUtil.getParentOfType(o, LSFMetaCodeStatement.class) == null
    }
    @Override
    public void visitMetaCodeStatement(@NotNull LSFMetaCodeStatement o) {
        super.visitMetaCodeStatement(o);

        checkReference(o);
    }

    public void visitMetaCodeDeclarationStatement(@NotNull LSFMetaCodeDeclarationStatement o) {
        super.visitMetaCodeDeclarationStatement(o);

        LSFAnyTokens statements = o.getAnyTokens();
        if(statements!=null) {
            Annotation annotation = myHolder.createInfoAnnotation(statements.getTextRange(), "");
            annotation.setEnforcedTextAttributes(META_DECL);
        }
    }

    private boolean checkReference(LSFReference reference) {
        Annotation errorAnnotation = reference.resolveErrorAnnotation(myHolder);
        if (!isInMetaDecl(reference) && errorAnnotation != null) {
            addError(reference, errorAnnotation);
            return false;
        }
        return true;
    }

    private void addError(LSFReference reference, Annotation annotation) {
        if (errorsSearchMode) {
            ShowErrorsAction.showErrorMessage(reference, annotation.getMessage());
        }
        TextAttributes error = annotation.getEnforcedTextAttributes() == null ? ERROR : annotation.getEnforcedTextAttributes();
        if(isInMetaUsage(reference))
            error = TextAttributes.merge(error, META_USAGE);
        annotation.setEnforcedTextAttributes(error);
    }

    private void addIndirectProp(LSFPropReference reference) {
        final Annotation annotation = myHolder.createWarningAnnotation(reference.getTextRange(), "Indirect usage");
        TextAttributes error = IMPLICIT_DECL;
        if(isInMetaUsage(reference))
            error = TextAttributes.merge(error, META_USAGE);
        annotation.setEnforcedTextAttributes(error);
    }

    private void addImplicitDecl(LSFReference reference) {
        final Annotation annotation = myHolder.createInfoAnnotation(reference.getTextRange(), "Implicit declaration");
        TextAttributes error = IMPLICIT_DECL;
        if(isInMetaUsage(reference))
            error = TextAttributes.merge(error, META_USAGE);
        annotation.setEnforcedTextAttributes(error);
    }
}

