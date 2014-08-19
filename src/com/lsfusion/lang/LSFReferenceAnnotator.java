package com.lsfusion.lang;

import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.lsfusion.actions.ShowErrorsAction;
import com.lsfusion.lang.meta.MetaNestingLineMarkerProvider;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ExtendParamContext;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.references.LSFPropReference;
import com.lsfusion.lang.psi.references.LSFReference;
import com.lsfusion.lang.typeinfer.MetaTypeInferAction;
import com.lsfusion.lang.typeinfer.TypeInferAction;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.Set;

import static com.lsfusion.util.JavaPsiUtils.hasSuperClass;

public class LSFReferenceAnnotator extends LSFVisitor implements Annotator {
    public static final String ACTION_PROPERTY_FQN = "lsfusion.server.logics.property.ActionProperty";

    public static final TextAttributes META_USAGE = new TextAttributes(null, new JBColor(Gray._239, Gray._61), null, null, Font.PLAIN);
    public static final TextAttributes META_NESTING_USAGE = new TextAttributes(new JBColor(Gray._180, Gray._91), null, null, null, Font.PLAIN);
    public static final TextAttributes META_DECL = new TextAttributes(null, new JBColor(new Color(255, 255, 192), new Color(37, 49, 37)), null, null, Font.PLAIN);
    public static final TextAttributes ERROR = new TextAttributes(new JBColor(new Color(255, 0, 0), new Color(188, 63, 60)), null, null, null, Font.PLAIN);
    public static final TextAttributes WAVE_UNDERSCORED_ERROR = new TextAttributes(null, null, new JBColor(new Color(255, 0, 0), new Color(188, 63, 60)), EffectType.WAVE_UNDERSCORE, Font.PLAIN);
    public static final TextAttributes IMPLICIT_DECL = new TextAttributes(Gray._96, null, null, null, Font.PLAIN);

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

    @Override
    public void visitElement(@NotNull PsiElement o) {
        if (o instanceof LeafPsiElement && isInMetaUsage(o)) { // фокус в том что побеждает наибольший приоритет, но важно следить что у верхнего правила всегда приоритет выше, так как в противном случае annotator просто херится
            Annotation annotation = myHolder.createInfoAnnotation(o.getTextRange(), null);
            annotation.setEnforcedTextAttributes(META_USAGE);
        }
    }

    @Override
    public void visitPropertyUsage(@NotNull LSFPropertyUsage o) {
        super.visitPropertyUsage(o);
        if (checkReference(o) && !o.isDirect())
            addIndirectProp(o);
    }

    @Override
    public void visitFormPropertyDrawUsage(@NotNull LSFFormPropertyDrawUsage o) {
        super.visitFormPropertyDrawUsage(o);
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

        if (o.resolveDecl() == o.getClassParamDeclare().getParamDeclare())
            addImplicitDecl(o);
    }

    @Override
    public void visitGroupObjectUsage(@NotNull LSFGroupObjectUsage o) {
        super.visitGroupObjectUsage(o);
        checkReference(o);
    }

    @Override
    public void visitFilterGroupUsage(@NotNull LSFFilterGroupUsage o) {
        super.visitFilterGroupUsage(o);
        checkReference(o);
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

        if (MetaNestingLineMarkerProvider.resolveNestingLevel(o) > 0) {
            Annotation annotation = myHolder.createInfoAnnotation(o.getMetaCodeStatementHeader().getTextRange(), "");
            annotation.setEnforcedTextAttributes(META_NESTING_USAGE);
            Annotation annotation2 = myHolder.createInfoAnnotation(o.getMetaCodeStatementSemi().getTextRange(), "");
            annotation2.setEnforcedTextAttributes(META_NESTING_USAGE);
            LSFMetaCodeBody metaCodeBody = o.getMetaCodeBody();
            if (metaCodeBody != null) {
                Annotation annotation3 = myHolder.createInfoAnnotation(metaCodeBody.getMetaCodeBodyLeftBrace().getTextRange(), "");
                annotation3.setEnforcedTextAttributes(META_NESTING_USAGE);
                Annotation annotation4 = myHolder.createInfoAnnotation(metaCodeBody.getMetaCodeBodyRightBrace().getTextRange(), "");
                annotation4.setEnforcedTextAttributes(META_NESTING_USAGE);
            }
        }
        checkReference(o);
    }

    public void visitMetaCodeDeclarationStatement(@NotNull LSFMetaCodeDeclarationStatement o) {
        super.visitMetaCodeDeclarationStatement(o);

        LSFAnyTokens statements = o.getAnyTokens();
        if (statements != null) {
            Annotation annotation = myHolder.createInfoAnnotation(statements.getTextRange(), "");
            annotation.setEnforcedTextAttributes(META_DECL);
        }
    }

    @Override
    public void visitPropertyStatement(@NotNull LSFPropertyStatement o) {
        super.visitPropertyStatement(o);

        if (o.resolveDuplicates()) {
            addAlreadyDefinedError(o.getPropertyDeclaration(), o.getPresentableText());
        } else {
            if (o.isStoredProperty()) {
                if (o.resolveDuplicateColumns()) {
                    addDuplicateColumnNameError(o.getNameIdentifier(), o.getTableName(), o.getColumnName());
                } else {
//                    addColumnInfo(o.getNameIdentifier(), o.getTableName(), o.getColumnName());
                }
            }
        }
    }

    @Override
    public void visitNavigatorElementDeclaration(@NotNull LSFNavigatorElementDeclaration o) {
        super.visitNavigatorElementDeclaration(o);

        checkAlreadyDefined(o);
    }

    @Override
    public void visitWindowDeclaration(@NotNull LSFWindowDeclaration o) {
        super.visitWindowDeclaration(o);

        checkAlreadyDefined(o);
    }

    @Override
    public void visitGroupDeclaration(@NotNull LSFGroupDeclaration o) {
        super.visitGroupDeclaration(o);

        checkAlreadyDefined(o);
    }

    @Override
    public void visitClassDeclaration(@NotNull LSFClassDeclaration o) {
        super.visitClassDeclaration(o);

        checkAlreadyDefined(o);
    }

    @Override
    public void visitFormDeclaration(@NotNull LSFFormDeclaration o) {
        super.visitFormDeclaration(o);

        checkAlreadyDefined(o);
    }

    @Override
    public void visitTableDeclaration(@NotNull LSFTableDeclaration o) {
        super.visitTableDeclaration(o);

        checkAlreadyDefined(o);
    }

    @Override
    public void visitMetaDeclaration(@NotNull LSFMetaDeclaration o) {
        super.visitMetaDeclaration(o);

        checkAlreadyDefined(o);
/*
        Annotation annotation = myHolder.createWarningAnnotation(o.getTextRange(), "Infer type");
        annotation.registerFix(new MetaTypeInferAction(o));*/
    }

    @Override
    public void visitClassExtend(@NotNull LSFClassExtend o) {
        super.visitClassExtend(o);

        Set<LSFStaticObjectDeclaration> staticObjectDuplicates = o.resolveStaticObjectDuplicates();
        for (LSFStaticObjectDeclaration so : staticObjectDuplicates) {
            addAlreadyDefinedError(so);
        }
    }

    @Override
    public void visitFormDecl(@NotNull LSFFormDecl o) {
        super.visitFormDecl(o);

        java.util.Set<? extends LSFDeclaration> duplicates = ((LSFFormExtend) o.getParent()).resolveDuplicates();
        for (LSFDeclaration decl : duplicates) {
            addAlreadyDefinedError(decl);
        }
    }

    @Override
    public void visitExtendingFormDeclaration(@NotNull LSFExtendingFormDeclaration o) {
        super.visitExtendingFormDeclaration(o);

        java.util.Set<? extends LSFDeclaration> duplicates = ((LSFFormExtend) o.getParent()).resolveDuplicates();
        for (LSFDeclaration decl : duplicates) {
            addAlreadyDefinedError(decl);
        }
    }

    @Override
    public void visitNonEmptyClassParamDeclareList(@NotNull LSFNonEmptyClassParamDeclareList o) {
        super.visitNonEmptyClassParamDeclareList(o);

        java.util.List<LSFClassParamDeclare> params = o.getClassParamDeclareList();
        for (int i = 0; i < params.size(); i++) {
            LSFId id1 = params.get(i).getParamDeclare().getNameIdentifier();
            for (int j = 0; j < params.size(); j++) {
                if (i != j) {
                    if (id1.getText().equals(params.get(j).getParamDeclare().getNameIdentifier().getText())) {
                        addAlreadyDefinedError(id1, id1.getText());
                        break;
                    }
                }
            }
        }
    }

    @Override
    public void visitLocalPropDeclaration(@NotNull LSFLocalPropDeclaration o) {
        super.visitLocalPropDeclaration(o);

        checkAlreadyDefined(o);
    }

    @Override
    public void visitJavaClassStringUsage(@NotNull LSFJavaClassStringUsage o) {
        super.visitJavaClassStringUsage(o);

        String elementText = o.getText();
        TextRange elementRange = o.getTextRange();

        for (PsiReference reference : o.getReferences()) {
            PsiElement resolved = reference.resolve();
            TextRange refRange = reference.getRangeInElement();
            TextRange refFileRange = TextRange.from(elementRange.getStartOffset() + refRange.getStartOffset(), refRange.getLength());
            if (resolved == null) {
                Annotation annotation = myHolder.createErrorAnnotation(refFileRange, "Can't resolve " + refRange.substring(elementText));
                annotation.setEnforcedTextAttributes(ERROR);
            } else if (refRange.getEndOffset() == elementRange.getLength() - 1) {
                //последний компонент должен быть ActionProperty
                boolean correctClass = resolved instanceof PsiClass && hasSuperClass((PsiClass) resolved, ACTION_PROPERTY_FQN);
                if (!correctClass) {
                    Annotation annotation = myHolder.createErrorAnnotation(refFileRange, "Class " + elementText + " should extend ActionProperty");
                    annotation.setEnforcedTextAttributes(ERROR);
                }
            }
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

    private void checkAlreadyDefined(LSFDeclaration declaration) {
        if (declaration.getName() != null && declaration.resolveDuplicates()) {
            addAlreadyDefinedError(declaration);
        }
    }
    
    private void addDuplicateColumnNameError(PsiElement element, String tableName, String columnName) {
        Annotation annotation = myHolder.createErrorAnnotation(element, "The property has duplicate column name. Table: " + tableName + ", column: " + columnName);
        annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
        addError(element, annotation);
    }

    private void addColumnInfo(PsiElement element, String tableName, String columnName) {
        final Annotation annotation = myHolder.createInfoAnnotation(element.getTextRange(), "Table: " + tableName + "; columnName: " + columnName);
        TextAttributes error = IMPLICIT_DECL;
        annotation.setEnforcedTextAttributes(error);
    }

    private void addAlreadyDefinedError(LSFDeclaration decl) {
        addAlreadyDefinedError(decl.getNameIdentifier(), decl.getPresentableText());
    }

    private void addAlreadyDefinedError(PsiElement element, String elementPresentableText) {
        Annotation annotation = myHolder.createErrorAnnotation(element, "'" + elementPresentableText + "' is already defined");
        annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
        addError(element, annotation);
    }

    private void addError(PsiElement element, Annotation annotation) {
        if (errorsSearchMode) {
            ShowErrorsAction.showErrorMessage(element, annotation.getMessage());
        }
        TextAttributes error = annotation.getEnforcedTextAttributes() == null ? ERROR : annotation.getEnforcedTextAttributes();
        if (isInMetaUsage(element))
            error = TextAttributes.merge(error, META_USAGE);
        annotation.setEnforcedTextAttributes(error);
    }

    private void addIndirectProp(LSFPropReference reference) {
        final Annotation annotation = myHolder.createWarningAnnotation(reference.getTextRange(), "Indirect usage");
        TextAttributes error = IMPLICIT_DECL;
        if (isInMetaUsage(reference))
            error = TextAttributes.merge(error, META_USAGE);
        annotation.setEnforcedTextAttributes(error);
    }

    private void addImplicitDecl(LSFReference reference) {
        final Annotation annotation = myHolder.createInfoAnnotation(reference.getTextRange(), "Implicit declaration");
        TextAttributes error = IMPLICIT_DECL;
        if (isInMetaUsage(reference))
            error = TextAttributes.merge(error, META_USAGE);
        annotation.setEnforcedTextAttributes(error);
    }
}

