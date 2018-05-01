package com.lsfusion.lang;

import com.intellij.lang.ASTNode;
import com.intellij.lang.Language;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Key;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.*;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.scope.PsiScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.SearchScope;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.actions.ShowErrorsAction;
import com.lsfusion.completion.ASTCompletionContributor;
import com.lsfusion.lang.classes.IntegralClass;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaNestingLineMarkerProvider;
import com.lsfusion.lang.meta.MetaTransaction;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ExprsContextModifier;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFPropertyStatementImpl;
import com.lsfusion.lang.psi.impl.LSFPropertyUsageImpl;
import com.lsfusion.lang.psi.references.*;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.refactoring.ElementMigration;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.lsfusion.util.JavaPsiUtils.hasSuperClass;

public class LSFReferenceAnnotator extends LSFVisitor implements Annotator {
    public static final String ACTION_PROPERTY_FQN = "lsfusion.server.logics.property.ActionProperty";

    public static final TextAttributes META_USAGE = new TextAttributes(null, new JBColor(Gray._239, Gray._61), null, null, Font.PLAIN);
    public static final TextAttributes META_NESTING_USAGE = new TextAttributes(new JBColor(Gray._180, Gray._91), null, null, null, Font.PLAIN);
    public static final TextAttributes META_DECL = new TextAttributes(null, new JBColor(new Color(255, 255, 192), new Color(37, 49, 37)), null, null, Font.PLAIN);
    public static final TextAttributes ERROR = new TextAttributes(new JBColor(new Color(255, 0, 0), new Color(188, 63, 60)), null, null, null, Font.PLAIN);
    public static final TextAttributes WAVE_UNDERSCORED_ERROR = new TextAttributes(null, null, new JBColor(new Color(255, 0, 0), new Color(188, 63, 60)), EffectType.WAVE_UNDERSCORE, Font.PLAIN);
    public static final TextAttributes WARNING = new TextAttributes(new JBColor(Gray._211, new Color(100, 100, 255)), null, null, null, Font.PLAIN);
    public static final TextAttributes WAVE_UNDERSCORED_WARNING = new TextAttributes(null, null, new JBColor(Gray._211, new Color(100, 100, 255)), EffectType.WAVE_UNDERSCORE, Font.PLAIN);
    public static final TextAttributes IMPLICIT_DECL = new TextAttributes(Gray._96, null, null, null, Font.PLAIN);
    public static final TextAttributes OUTER_PARAM = new TextAttributes(new JBColor(new Color(102, 14, 122), new Color(152, 118, 170)), null, null, null, Font.PLAIN);
    public static final TextAttributes UNTYPED_IMPLICIT_DECL = new TextAttributes(new JBColor(new Color(56, 96, 255), new Color(100, 100, 255)), null, null, null, Font.PLAIN);

    private AnnotationHolder myHolder;
    public boolean errorsSearchMode = false;
    public boolean warningsSearchMode = false;

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
        checkIndirectUsage(o);
    }

    @Override
    public void visitActionUsage(@NotNull LSFActionUsage o) {
        super.visitActionUsage(o);
        checkIndirectUsage(o);
    }

    @Override
    public void visitPropertyElseActionUsage(@NotNull LSFPropertyElseActionUsage o) {
        super.visitPropertyElseActionUsage(o);
        checkIndirectUsage(o);
    }

    public void checkIndirectUsage(@NotNull LSFActionOrPropReference o) {
        if (checkReference(o) && !o.isDirect())
            addIndirectProp(o);
    }

    @Override
    public void visitFormPropertyDrawUsage(@NotNull LSFFormPropertyDrawUsage o) {
        super.visitFormPropertyDrawUsage(o);
        checkReference(o);
    }

    @Override
    public void visitFormElseNoParamsActionReference(@NotNull LSFFormElseNoParamsActionReference o) {
        super.visitFormElseNoParamsActionReference(o);
        checkReference(o);
    }

    @Override
    public void visitComponentUsage(@NotNull LSFComponentUsage o) {
        super.visitComponentUsage(o);
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

        LSFClassName className = o.getClassParamDeclare().getClassName();
        if (o.resolveDecl() == o.getClassParamDeclare().getParamDeclare()) {
            if (className != null)
                addImplicitDecl(o);
            else
                addUntypedImplicitDecl(o);
        }

        if (className != null) {
            LSFExprParamReference parentRef = PsiTreeUtil.getParentOfType(o.resolveDecl(), LSFExprParamReference.class);
            if (parentRef == null || o != parentRef) {
                Annotation annotation = myHolder.createErrorAnnotation(o, "Redefinition of reference '" + o.getNameRef() + "'");
                annotation.setEnforcedTextAttributes(LSFReferenceAnnotator.WAVE_UNDERSCORED_ERROR);
                addError(o, annotation);
            }
        } else {
            LSFPropertyExprObject pExprObject = PsiTreeUtil.getParentOfType(o, LSFPropertyExprObject.class);
            if(pExprObject != null) {
                LSFExprParamDeclaration decl = o.resolveDecl();
                if(decl != null && isOuter(decl, pExprObject))
                    addOuterRef(o);
            }
        }
    }

    private void addOuterRef(LSFReference reference) {
        final Annotation annotation = myHolder.createInfoAnnotation(reference.getTextRange(), "Outer param");
        TextAttributes error = OUTER_PARAM;
        if (isInMetaUsage(reference))
            error = TextAttributes.merge(error, META_USAGE);
        annotation.setEnforcedTextAttributes(error);
    }

    public static boolean isOuter(LSFExprParamDeclaration decl, LSFPropertyExprObject pExprObject) {
        if(decl.getTextOffset() < pExprObject.getTextOffset())
            return true;

        if(decl instanceof LSFObjectDeclaration) // если объект то может быть и позже
            return decl.getTextOffset() > pExprObject.getTextOffset() + pExprObject.getTextLength();

        return false;
    }

    public static boolean isOuter(LSFExprParamDeclaration decl, PsiElement pExprObject) {
        if(decl.getTextOffset() < pExprObject.getTextOffset())
            return true;

        return false;
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
            Annotation metaHeaderAnnotation = myHolder.createInfoAnnotation(o.getMetaCodeStatementHeader().getTextRange(), "");
            metaHeaderAnnotation.setEnforcedTextAttributes(META_NESTING_USAGE);
            LSFMetaCodeStatementSemi metaCodeStatementSemi = o.getMetaCodeStatementSemi();
            if (metaCodeStatementSemi != null) {
                Annotation metaSemiAnnotation = myHolder.createInfoAnnotation(metaCodeStatementSemi.getTextRange(), "");
                metaSemiAnnotation.setEnforcedTextAttributes(META_NESTING_USAGE);
            }
            LSFMetaCodeBody metaCodeBody = o.getMetaCodeBody();
            if (metaCodeBody != null) {
                Annotation metaLeftBraceAnnotation = myHolder.createInfoAnnotation(metaCodeBody.getMetaCodeBodyLeftBrace().getTextRange(), "");
                metaLeftBraceAnnotation.setEnforcedTextAttributes(META_NESTING_USAGE);
                Annotation metaRightBraceAnnotation = myHolder.createInfoAnnotation(metaCodeBody.getMetaCodeBodyRightBrace().getTextRange(), "");
                metaRightBraceAnnotation.setEnforcedTextAttributes(META_NESTING_USAGE);
            }
        }
        checkReference(o);
    }
    
    public void visitMetaCodeDeclarationStatement(@NotNull LSFMetaCodeDeclarationStatement o) {
        super.visitMetaCodeDeclarationStatement(o);
        if (o.getMetaDeclIdList() == null || o.getParamCount() == 0) {
            int leftBrace = o.getText().indexOf('(');
            int rightBrace = o.getText().indexOf(')');
            if (rightBrace > leftBrace && leftBrace >= 0) {
                TextRange bracesRange = new TextRange(o.getTextRange().getStartOffset() + leftBrace, o.getTextRange().getStartOffset() + rightBrace + 1);
                Annotation annotation = myHolder.createInfoAnnotation(bracesRange, "Metacode should have at least one parameter");
                annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
                addError(o, annotation);
            }
        }
        
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

        LSFPropertyDeclParams propertyDeclParams = o.getPropertyDeclaration().getPropertyDeclParams();
        if(propertyDeclParams != null) {
            LSFPropertyCalcStatement propertyCalcStatement = o.getPropertyCalcStatement();
            if (propertyCalcStatement != null) {
                LSFExpressionUnfriendlyPD expressionUnfriendlyPD = propertyCalcStatement.getExpressionUnfriendlyPD();
                if (expressionUnfriendlyPD != null) {
                    LSFGroupPropertyDefinition groupPropertyDefinition = expressionUnfriendlyPD.getGroupPropertyDefinition();
                    if (groupPropertyDefinition != null) {
                        LSFGroupPropertyBy groupPropertyBy = groupPropertyDefinition.getGroupPropertyBy();
                        if (groupPropertyBy != null) {
                            List<LSFParamDeclaration> declareParams = LSFPsiImplUtil.resolveParams(propertyDeclParams.getClassParamDeclareList());
                            Pair<List<LSFParamDeclaration>, Map<LSFPropertyExpression, Pair<LSFClassSet, LSFClassSet>>> incorrect = LSFPsiImplUtil.checkValueParamClasses(groupPropertyDefinition, declareParams);

                            for (LSFParamDeclaration incParam : incorrect.first) {
                                Annotation annotation = myHolder.createErrorAnnotation(incParam, "Not used / No implementation found in BY clause found");
                                annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
                                addError(incParam, annotation);
                            }

                            for (Map.Entry<LSFPropertyExpression, Pair<LSFClassSet, LSFClassSet>> incBy : incorrect.second.entrySet()) {
                                Annotation annotation;
                                if (incBy.getValue() != null)
                                    annotation = myHolder.createErrorAnnotation(incBy.getKey(),
                                            String.format("Incorrect GROUP BY param: required %s; found %s", incBy.getValue().second, incBy.getValue().first));
                                else
                                    annotation = myHolder.createErrorAnnotation(incBy.getKey(), "No param for this BY clause found");
                                annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
                                addError(incBy.getKey(), annotation);
                            }
                        }
                    }
                } else {
                    LSFPropertyExpression propertyExpression = propertyCalcStatement.getPropertyExpression();
                    List<LSFParamDeclaration> declareParams = LSFPsiImplUtil.resolveParams(propertyDeclParams.getClassParamDeclareList());
                    Set<String> usedParameter = new ExprsContextModifier(propertyExpression).resolveUsedParams();
                    for(LSFParamDeclaration declareParam : declareParams)
                        if(!usedParameter.contains(declareParam.getName())) {
                            final Annotation annotation = myHolder.createWarningAnnotation(declareParam, "Parameter is not used");
                            TextAttributes error = UNTYPED_IMPLICIT_DECL;
                            if (isInMetaUsage(declareParam))
                                error = TextAttributes.merge(error, META_USAGE);
                            annotation.setEnforcedTextAttributes(error);
                        }   
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
    public void visitStaticObjectReference(@NotNull LSFStaticObjectReference o) {
        super.visitStaticObjectReference(o);

        checkReference(o);
    }

    @Override
    public void visitRelationalPE(@NotNull LSFRelationalPE o) {
        super.visitRelationalPE(o);

        checkRelationalPE(o);
    }

    @Override
    public void visitEqualityPE(@NotNull LSFEqualityPE o) {
        super.visitEqualityPE(o);

        checkEqualityPE(o);
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

    public void visitStringValueLiteral(@NotNull LSFStringValueLiteral o) {
        super.visitStringValueLiteral(o);
        checkEscapeSequences(o, "nrt'\\");
    }

    public void visitLocalizedStringValueLiteral(@NotNull LSFLocalizedStringValueLiteral o) {
        super.visitLocalizedStringValueLiteral(o);
        checkEscapeSequences(o, "nrt'\\{}");
    }

    private void checkEscapeSequences(PsiElement element, final String escapedSymbols) {
        String text = element.getText();
        for (int i = 0; i < text.length(); ++i) {
            char curCh = text.charAt(i);
            if (curCh == '\\') {
                if (i + 1 < text.length() && !escapedSymbols.contains(text.substring(i + 1, i + 2))) {
                    TextRange textRange = TextRange.create(element.getTextRange().getStartOffset() + i, element.getTextRange().getStartOffset() + i + 2);
                    Annotation annotation = myHolder.createErrorAnnotation(textRange, "Wrong escape sequence " + text.substring(i, i + 2));
                    annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
                    addError(element, annotation);
                } else {
                    ++i;
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

    private void checkRelationalPE(LSFRelationalPE relationalPE) {
        List<LSFLikePE> children = relationalPE.getLikePEList();
        if (children.size() == 2) {
            LSFClassSet class1 = getLSFClassSet(children.get(0));
            LSFClassSet class2 = getLSFClassSet(children.get(1));
            if (class1 != null && class2 != null && !class1.isCompatible(class2)) {
                Annotation annotation = myHolder.createErrorAnnotation(relationalPE, String.format("Type mismatch: can't compare %s and %s", class1, class2));
                annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
                addError(relationalPE, annotation);
            }
        } else {
            LSFTypePropertyDefinition type = relationalPE.getTypePropertyDefinition();
            if (type != null && relationalPE.getChildren().length == 2) {
                LSFClassSet class1 = getLSFClassSet(children.get(0));
                LSFClassSet class2 = LSFPsiImplUtil.resolveClass(type.getClassName());
                if (class1 != null && class2 != null && !class1.haveCommonChildren(class2, null)) {
                    myHolder.createWarningAnnotation(relationalPE, String.format("Type mismatch: can't cast %s to %s", class1, class2));
                }
            }
        }
    }

    private LSFClassSet getLSFClassSet(LSFLikePE element) {
        return LSFExClassSet.fromEx(element.resolveInferredValueClass(null));
    }

    private void checkEqualityPE(LSFEqualityPE equalityPE) {
        List<LSFRelationalPE> children = equalityPE.getRelationalPEList();
        if (children.size() == 2) {
            LSFClassSet class1 = getLSFClassSet(children.get(0));
            LSFClassSet class2 = getLSFClassSet(children.get(1));
            if (class1 != null && class2 != null && !class1.isCompatible(class2)) {
                Annotation annotation = myHolder.createErrorAnnotation(equalityPE, String.format("Type mismatch: can't compare %s and %s", class1, class2));
                annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
                addError(equalityPE, annotation);
            }
        }
    }

    private LSFClassSet getLSFClassSet(LSFRelationalPE element) {
        return LSFExClassSet.fromEx(element.resolveInferredValueClass(null));
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
        annotation.setEnforcedTextAttributes(IMPLICIT_DECL);
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
        addError(element, annotation, LSFErrorLevel.ERROR);
    }

    private void addError(PsiElement element, Annotation annotation, LSFErrorLevel errorLevel) {
        if ((errorLevel == LSFErrorLevel.WARNING && warningsSearchMode) || (errorLevel == LSFErrorLevel.ERROR && errorsSearchMode)) {
            ShowErrorsAction.showErrorMessage(element, annotation.getMessage(), errorLevel);
        }
        TextAttributes error = annotation.getEnforcedTextAttributes() == null ? (errorLevel == LSFErrorLevel.ERROR ? ERROR : WARNING) : annotation.getEnforcedTextAttributes();
        if (isInMetaUsage(element))
            error = TextAttributes.merge(error, META_USAGE);
        annotation.setEnforcedTextAttributes(error);
    }

    private void addIndirectProp(LSFActionOrPropReference reference) {
        final Annotation annotation = myHolder.createWeakWarningAnnotation(reference.getTextRange(), "Indirect usage");
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

    private void addUntypedImplicitDecl(LSFReference reference) {
        final Annotation annotation = myHolder.createInfoAnnotation(reference.getTextRange(), "Untyped implicit declaration");
        TextAttributes error = UNTYPED_IMPLICIT_DECL;
        if (isInMetaUsage(reference))
            error = TextAttributes.merge(error, META_USAGE);
        annotation.setEnforcedTextAttributes(error);
    }

    @Override
    public void visitOverridePropertyStatement(@NotNull LSFOverridePropertyStatement element) {
        Result<LSFClassSet> required = new Result<>();
        Result<LSFClassSet> found = new Result<>();
        if (!LSFPsiImplUtil.checkOverrideValue(element, required, found)) {
            String requiredClass = required.getResult() instanceof LSFValueClass ? ((LSFValueClass) required.getResult()).getCaption() : required.getResult().getCanonicalName();
            String foundClass = found.getResult() instanceof LSFValueClass ? ((LSFValueClass) found.getResult()).getCaption() : found.getResult().getCanonicalName();
            Annotation annotation = myHolder.createErrorAnnotation(element, "Wrong value class. Required : " + requiredClass + ", found : " + foundClass);
            annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
            addError(element, annotation);
        }

        if (!LSFPsiImplUtil.checkNonRecursiveOverride(element)) {
            Annotation annotation = myHolder.createErrorAnnotation(element, "Recursive implement");
            annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
            addError(element, annotation);
        }
    }
    @Override
    public void visitOverrideActionStatement(@NotNull LSFOverrideActionStatement element) {
        if (!LSFPsiImplUtil.checkNonRecursiveOverride(element)) {
            Annotation annotation = myHolder.createErrorAnnotation(element, "Recursive implement");
            annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
            addError(element, annotation);
        }
    }


    @Override
    public void visitAssignActionPropertyDefinitionBody(@NotNull LSFAssignActionPropertyDefinitionBody o) {
        LSFPropertyUsageImpl propertyUsage = (LSFPropertyUsageImpl) o.getFirstChild().getFirstChild();
        if (propertyUsage != null) {
            LSFPropDeclaration declaration = propertyUsage.resolveDecl();
            if (declaration != null) {
                if (declaration instanceof LSFPropertyStatementImpl) {
                    LSFPropertyCalcStatement propertyStatement = ((LSFPropertyStatementImpl) declaration).getPropertyCalcStatement();
                    if (propertyStatement == null) {
                        addAssignError(o);
                    } else {
                        LSFPropertyExpression expression = propertyStatement.getPropertyExpression();
                        if (expression != null && !assignAllowed(expression)) {
                            addAssignError(o);
                        } else {
                            LSFExpressionUnfriendlyPD expressionUnfriendlyPD = propertyStatement.getExpressionUnfriendlyPD();
                            if (expressionUnfriendlyPD != null) {
                                LSFGroupPropertyDefinition groupPD = expressionUnfriendlyPD.getGroupPropertyDefinition();
                                if (groupPD != null)
                                    addAssignError(o);
                            }
                        }
                    }
                    LSFClassSet leftClass = declaration.resolveValueClass();
                    List<LSFPropertyExpression> rightPropertyExpressionList = o.getPropertyExpressionList();
                    if (!rightPropertyExpressionList.isEmpty()) {
                        LSFClassSet rightClass = LSFExClassSet.fromEx(rightPropertyExpressionList.get(0).resolveValueClass(false));
                        if (leftClass != null && rightClass != null) {
                            if (!leftClass.isCompatible(rightClass))
                                addTypeMismatchError(o, rightClass, leftClass);
                        }
                    }
                }
            }
        }
    }

    private boolean assignAllowed(PsiElement element) {
        if (element instanceof LSFOverridePropertyDefinition || element instanceof LSFMultiPropertyDefinition || element instanceof LSFCasePropertyDefinition)
            return true;
        for (PsiElement child : element.getChildren()) {
            if (assignAllowed(child))
                return true;
        }
        return false;
    }

    private void addAssignError(LSFAssignActionPropertyDefinitionBody o) {
        Annotation annotation = myHolder.createErrorAnnotation(o, "ASSIGN is allowed only to DATA/MULTI/CASE property");
        annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
        addError(o, annotation);
    }

    private void addTypeMismatchError(LSFAssignActionPropertyDefinitionBody o, LSFClassSet class1, LSFClassSet class2) {
        Annotation annotation = myHolder.createErrorAnnotation(o, String.format("Type mismatch: can't cast %s to %s", class1.getCanonicalName(), class2.getCanonicalName()));
        annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
        addError(o, annotation, LSFErrorLevel.ERROR);
    }

    @Override
    public void visitSetObjectPropertyStatement(@NotNull LSFSetObjectPropertyStatement o) {
        String text = o.getText();
        if (text != null && text.contains("=")) {
            String property = text.substring(0, text.indexOf("=")).trim();
            if (!ASTCompletionContributor.validDesignProperty(property)) {
                Annotation annotation = myHolder.createErrorAnnotation(o, "Can't resolve property " + property);
                annotation.setEnforcedTextAttributes(ERROR);
                addError(o, annotation);
            }
        }
    }

    @Override
    public void visitTypeMult(@NotNull LSFTypeMult o) {
        for (PsiElement child : o.getParent().getChildren()) {
            if (child instanceof LSFUnaryMinusPE) {
                LSFClassSet classSet = LSFExClassSet.fromEx(((LSFUnaryMinusPE) child).resolveInferredValueClass(null));
                if (classSet != null && !(classSet instanceof IntegralClass)) {
                    Annotation annotation = myHolder.createErrorAnnotation(child, "Can't multiply / divide " + classSet + " value");
                    annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
                    addError(child, annotation);
                }
            }
        }
    }

    @Override
    public void visitAdditiveORPE(@NotNull LSFAdditiveORPE o) {
        List<LSFAdditivePE> additivePEList = o.getAdditivePEList();
        if (additivePEList.size() > 1) {
            for (LSFAdditivePE child : additivePEList) {
                LSFClassSet classSet = LSFExClassSet.fromEx(child.resolveInferredValueClass(null));
                if (classSet != null && !(classSet instanceof IntegralClass)) {
                    Annotation annotation = myHolder.createErrorAnnotation(child, "Can't add / subtract " + classSet + " value");
                    annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_ERROR);
                    addError(child, annotation);
                }
            }
        }
    }

    @Override
    public void visitModuleUsage(@NotNull LSFModuleUsage moduleUsage) {
        if (moduleUsage.resolveDecl() == null) {
            addError(moduleUsage, moduleUsage.resolveErrorAnnotation(myHolder));
        }
    }
}
