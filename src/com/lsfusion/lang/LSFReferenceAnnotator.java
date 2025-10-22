package com.lsfusion.lang;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.lang.annotation.AnnotationBuilder;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.lang.annotation.HighlightSeverity;
import com.intellij.lang.properties.psi.PropertiesFile;
import com.intellij.lang.properties.references.I18nUtil;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.command.CommandProcessor;
import com.intellij.openapi.command.undo.UndoUtil;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.markup.EffectType;
import com.intellij.openapi.editor.markup.TextAttributes;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Pair;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiReference;
import com.intellij.psi.impl.source.tree.LeafPsiElement;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.ui.Gray;
import com.intellij.ui.JBColor;
import com.intellij.util.IncorrectOperationException;
import com.lsfusion.actions.ShowErrorsAction;
import com.lsfusion.completion.ASTCompletionContributor;
import com.lsfusion.design.model.ContainerType;
import com.lsfusion.design.model.FontInfo;
import com.lsfusion.design.ui.FlexAlignment;
import com.lsfusion.lang.classes.*;
import com.lsfusion.lang.meta.MetaNestingLineMarkerProvider;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ExprsContextModifier;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.impl.LSFClassDeclImpl;
import com.lsfusion.lang.psi.impl.LSFFormDeclImpl;
import com.lsfusion.lang.psi.impl.LSFLocalPropertyDeclarationNameImpl;
import com.lsfusion.lang.psi.impl.LSFPropertyUsageImpl;
import com.lsfusion.lang.psi.references.*;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.util.BaseUtils;
import com.lsfusion.util.LSFStringUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;

import static com.lsfusion.util.JavaPsiUtils.hasSuperClass;
import static java.lang.String.format;

public class LSFReferenceAnnotator extends LSFVisitor implements Annotator {
    public static final String ACTION_FQN = "lsfusion.server.logics.action.Action";

    public static final TextAttributes META_USAGE = new TextAttributes(null, new JBColor(Gray._239, Gray._61), null, null, Font.PLAIN);
    public static final TextAttributes META_DECL = new TextAttributes(null, new JBColor(new Color(255, 255, 192), new Color(37, 49, 37)), null, null, Font.PLAIN);

    public static final TextAttributes META_DECL_USAGE = new TextAttributes(null, new JBColor(new Color(239, 239, 207), new Color(49, 55, 49)), null, null, Font.PLAIN);
    public static final TextAttributes META_ERROR = new TextAttributes(new JBColor(new Color(255, 128, 0), new Color(112, 48, 48)), null, null, null, Font.PLAIN);
    public static final TextAttributes WAVE_UNDERSCORED_META_ERROR = new TextAttributes(null, null, new JBColor(new Color(255, 128, 0), new Color(112, 48, 48)), EffectType.WAVE_UNDERSCORE, Font.PLAIN);

    public static final TextAttributes META_NESTING_USAGE = new TextAttributes(new JBColor(Gray._180, Gray._91), null, null, null, Font.PLAIN);
    public static final TextAttributes ERROR = new TextAttributes(new JBColor(new Color(255, 0, 0), new Color(188, 63, 60)), null, null, null, Font.PLAIN);
    public static final TextAttributes WAVE_UNDERSCORED_ERROR = new TextAttributes(null, null, new JBColor(new Color(255, 0, 0), new Color(188, 63, 60)), EffectType.WAVE_UNDERSCORE, Font.PLAIN);
    public static final TextAttributes WARNING = new TextAttributes(new JBColor(Gray._211, new Color(100, 100, 255)), null, null, null, Font.PLAIN);
    public static final TextAttributes WAVE_UNDERSCORED_WARNING = new TextAttributes(null, null, new JBColor(Gray._211, new Color(100, 100, 255)), EffectType.WAVE_UNDERSCORE, Font.PLAIN);
    public static final TextAttributes IMPLICIT_DECL = new TextAttributes(Gray._96, null, null, null, Font.PLAIN);
    public static final TextAttributes OUTER_PARAM = new TextAttributes(new JBColor(new Color(102, 14, 122), new Color(152, 118, 170)), null, null, null, Font.PLAIN);
    public static final TextAttributes UNTYPED_IMPLICIT_DECL = new TextAttributes(new JBColor(new Color(56, 96, 255), new Color(100, 100, 255)), null, null, null, Font.PLAIN);
    public static final TextAttributes DEPRECATED_WARNING = new TextAttributes(null, null, JBColor.BLACK, EffectType.STRIKEOUT, Font.PLAIN);

    private AnnotationHolder myHolder;
    public boolean errorsSearchMode = false;
    public boolean warningsSearchMode = false;

    @Override
    public synchronized void annotate(@NotNull PsiElement psiElement, AnnotationHolder holder) {
        if (psiElement instanceof LSFElement lsfElement && lsfElement.isInLibrarySources()) {
            return;
        }

        myHolder = holder;
        try {
            psiElement.accept(this);
        } finally {
            myHolder = null;
        }
    }

    @Override
    public void visitElement(@NotNull PsiElement o) {
        if (o instanceof LeafPsiElement) { // фокус в том что побеждает наибольший приоритет, но важно следить что у верхнего правила всегда приоритет выше, так как в противном случае annotator просто херится
            TextAttributes textAttributes = mergeMetaAttributes(o, null);
            if (textAttributes != null) {
                addSilentInfoAnnotation(o, textAttributes);
            }
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
    public void visitConcatPropertyDefinition(@NotNull LSFConcatPropertyDefinition o) {
        super.visitConcatPropertyDefinition(o);
        LSFPropertyExpression separatorExpression = o.getPropertyExpression();
        if (separatorExpression != null) {
            LSFExClassSet type = separatorExpression.resolveValueClass(false);
            if (type != null && !(type.classSet instanceof StringClass)) {
                addUnderscoredError(separatorExpression, "Separator should be STRING");
            }
        }
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
    public void visitIndexedSetting(@NotNull LSFIndexedSetting o) {
        super.visitIndexedSetting(o);
        LSFNonEmptyPropertyOptions options = (LSFNonEmptyPropertyOptions) o.getParent();
        PsiElement propertyStatement = options.getParent();
        if (propertyStatement instanceof LSFPropertyStatement && !((LSFPropertyStatement) propertyStatement).isStoredProperty()) {
            addUnderscoredError(o, "INDEXED property should be materialized");
        }
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

    @Override
    public void visitPropertyExprObject(@NotNull LSFPropertyExprObject o) {
        super.visitPropertyExprObject(o);

        LSFPropertyCalcStatement propStatement = o.getPropertyCalcStatement();
        if(propStatement != null) {
            LSFExpressionUnfriendlyPD unfriend = propStatement.getExpressionUnfriendlyPD();
            if(unfriend != null) {
                if(unfriend.getAggrPropertyDefinition() != null || unfriend.getDataPropertyDefinition() != null) {
                    addUnderscoredError(o, "This operator cannot be used in []");
                }                    
            }
        }
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
            if (o != parentRef && !isInMetaDecl(o)) {
                addUnderscoredError(o, "Redefinition of reference '" + o.getNameRef() + "'");
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

    private void addOuterRef(PsiElement element) {
        addWarningAnnotation(element, "Outer param", mergeMetaAttributes(element, OUTER_PARAM), null);
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

    public void visitGroupObjectTreeSingleSelectorType(@NotNull LSFGroupObjectTreeSingleSelectorType o) {
        super.visitGroupObjectTreeSingleSelectorType(o);
        String text = o.getText();
        if (text != null) {
            if (text.equals("USERFILTER")) {
                addDeprecatedWarningAnnotation(o, "5.2", "6.0", "Use FILTERS container instead");
            } else if (text.equals("GRIDBOX")) {
                addDeprecatedWarningAnnotation(o, "5.2", "6.0", "Use GRID container instead");
            }
        }
    }

    @Override
    public void visitCustomOptionsLiteral(@NotNull LSFCustomOptionsLiteral o) {
        super.visitCustomOptionsLiteral(o);
        if(o.getText().equals("HEADER"))
            addDeprecatedWarningAnnotation(o, "5.2", "6.0", "Use OPTIONS instead");
    }

    @Override
    public void visitGroupObjectUsage(@NotNull LSFGroupObjectUsage o) {
        super.visitGroupObjectUsage(o);
        checkReference(o);
    }

    @Override
    public void visitGroupSelector(@NotNull LSFGroupSelector o) {
        super.visitGroupSelector(o);
        LSFGroupDeclaration groupStatement = o.getGroupUsage().resolveDecl();
        if(groupStatement instanceof LSFGroupStatement) {
            if (((LSFGroupStatement) groupStatement).getNativeLiteral() != null) {
                addUnderscoredError(o, "Group " + o.getText() + " is NATIVE, group containers are not created for it");
            }
        }
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

    public static LSFMetaCodeDeclarationStatement getMetaDecl(PsiElement o) {
        return PsiTreeUtil.getParentOfType(o, LSFMetaCodeDeclarationStatement.class);
    }

    public static boolean isInMetaDecl(PsiElement o) {
        return getMetaDecl(o) != null;
        //&& PsiTreeUtil.getParentOfType(o, LSFMetaCodeStatement.class) == null
    }

    private void checkMetaNestingUsage(@NotNull PsiElement o) {
        if (MetaNestingLineMarkerProvider.resolveNestingLevel(o) > 1) {
            addSilentInfoAnnotation(o, META_NESTING_USAGE);
        }
    }

    @Override
    public void visitMetaCodeStatementHeader(@NotNull LSFMetaCodeStatementHeader o) {
        super.visitMetaCodeStatementHeader(o);

        checkMetaNestingUsage(o);
    }

    @Override
    public void visitMetaCodeStatementSemi(@NotNull LSFMetaCodeStatementSemi o) {
        super.visitMetaCodeStatementSemi(o);

        checkMetaNestingUsage(o);
    }

    @Override
    public void visitMetaCodeBodyLeftBrace(@NotNull LSFMetaCodeBodyLeftBrace o) {
        super.visitMetaCodeBodyLeftBrace(o);

        checkMetaNestingUsage(o);
    }

    @Override
    public void visitMetaCodeBodyRightBrace(@NotNull LSFMetaCodeBodyRightBrace o) {
        super.visitMetaCodeBodyRightBrace(o);

        checkMetaNestingUsage(o);
    }

    @Override
    public void visitMetaCodeStatement(@NotNull LSFMetaCodeStatement o) {
        super.visitMetaCodeStatement(o);

        checkReference(o);
    }
    
    public void visitMetaCodeDeclarationStatement(@NotNull LSFMetaCodeDeclarationStatement o) {
        super.visitMetaCodeDeclarationStatement(o);
        if (o.getMetaDeclIdList() == null || o.getParamCount() == 0) {
            int leftBrace = o.getText().indexOf('(');
            int rightBrace = o.getText().indexOf(')');
            if (rightBrace > leftBrace && leftBrace >= 0) {
                addUnderscoredError(o, new TextRange(o.getTextRange().getStartOffset() + leftBrace, o.getTextRange().getStartOffset() + rightBrace + 1), "Metacode should have at least one parameter");
            }
        }
    }

    @Override
    public void visitActionStatement(@NotNull LSFActionStatement o) {
        super.visitActionStatement(o);

        checkAlreadyDefined(o);
    }
    
    @Override
    public void visitPropertyStatement(@NotNull LSFPropertyStatement o) {
        super.visitPropertyStatement(o);

        checkAlreadyDefined(o);

        if (o.isStoredProperty()) {
            if (o.resolveDuplicateColumns()) {
                addDuplicateColumnNameError(o.getNameIdentifier(), o.getTableName(), o.getColumnName());
            }
        }

        LSFPropertyDeclParams propertyDeclParams = o.getPropertyDeclaration().getPropertyDeclParams();
        if(propertyDeclParams != null) {
            LSFPropertyCalcStatement propertyCalcStatement = o.getPropertyCalcStatement();
            LSFExpressionUnfriendlyPD expressionUnfriendlyPD = propertyCalcStatement.getExpressionUnfriendlyPD();
            if (expressionUnfriendlyPD != null) {
                List<LSFParamDeclaration> declareParams = LSFPsiImplUtil.resolveParams(propertyDeclParams.getClassParamDeclareList());
                Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> incorrect = expressionUnfriendlyPD.checkValueParamClasses(declareParams);

                //disable for formula
                if(propertyCalcStatement.getExpressionUnfriendlyPD().getFormulaPropertyDefinition() == null) {
                    for (LSFParamDeclaration incParam : incorrect.first) {
                        addUnderscoredError(incParam, "Not used / No implementation found");
                    }
                }

                for (Map.Entry<PsiElement, Pair<LSFClassSet, LSFClassSet>> incBy : incorrect.second.entrySet()) {
                    addUnderscoredError(incBy.getKey(), incBy.getValue() != null ?
                            String.format("Incorrect param implementation: required %s; found %s", incBy.getValue().second, incBy.getValue().first) :
                            "No param for this implementation found");
                }
            } else {
                LSFPropertyExpression propertyExpression = propertyCalcStatement.getPropertyExpression();
                List<LSFParamDeclaration> declareParams = LSFPsiImplUtil.resolveParams(propertyDeclParams.getClassParamDeclareList());
                Set<String> usedParameter = new ExprsContextModifier(propertyExpression).resolveAllParams();
                for(LSFParamDeclaration declareParam : declareParams)
                    if(!usedParameter.contains(declareParam.getName())) {
                        addWarningAnnotation(declareParam, "Parameter is not used");
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
    public void visitPostfixUnaryPE(@NotNull LSFPostfixUnaryPE o) {
        super.visitPostfixUnaryPE(o);
        
        checkPostfixPE(o);
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
        checkAutorefresh(o);
    }

    private void checkAutorefresh(LSFFormDeclaration o) {
        for(LSFAutorefreshLiteral autorefresh : ((LSFFormDeclImpl) o).getAutorefreshLiteralList()) {
            addDeprecatedWarningAnnotation(autorefresh, "5.2", "6.0", "Use EVENTS ON SCHEDULE PERIOD n formRefresh() instead");
        }
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
    }

    @Override
    public void visitClassExtend(@NotNull LSFClassExtend o) {
        super.visitClassExtend(o);

        checkAlreadyDefined(o);
        checkRecursiveExtend(o);
    }

    private void checkAlreadyDefined(@NotNull LSFExtend o) {
        for (LSFDeclaration duplicate : (Set<LSFDeclaration>) o.resolveDuplicates()) {
            addAlreadyDefinedError(duplicate);
        }
    }

    private void checkRecursiveExtend(@NotNull LSFExtend o) {
        LSFFullNameDeclaration decl = o.resolveDecl();
        if(decl instanceof LSFClassDeclaration) {

            Set<LSFValueClass> classes = new HashSet<>();

            if(checkRecursiveExtend((LSFClassDeclaration) decl, classes)) {
                addUnderscoredError(o, "Recursive EXTEND CLASS");
            }
        }
    }

    private boolean checkRecursiveExtend(LSFClassDeclaration decl, Set<LSFValueClass> classes) {
        for (LSFClassDeclaration parent : CustomClassSet.getParents(decl)) {
            if (!classes.contains(parent)) {
                classes.add(parent);
                return checkRecursiveExtend(parent, classes);
            } else {
                return true;
            }
        }
        return false;
    }

    @Override
    public void visitFormStatement(@NotNull LSFFormStatement o) {
        super.visitFormStatement(o);

        checkAlreadyDefined(o);
    }

    @Override
    public void visitExternalActionPropertyDefinitionBody(@NotNull LSFExternalActionPropertyDefinitionBody o) {
        super.visitExternalActionPropertyDefinitionBody(o);

        //todo: remove in 7.0
        if(o.getText().startsWith("EXTERNAL SQL 'LOCAL'")) {
            LSFPropertyExpression expr = o.getPropertyExpressionList().get(0);
            addErrorAnnotation(expr, expr.getTextRange(), "Use INTERNAL DB instead");
        }
    }

    @Override
    public void visitDesignStatement(@NotNull LSFDesignStatement o) {
        super.visitDesignStatement(o);

        checkAlreadyDefined(o);
    }

    @Override
    public void visitDrawRoot(@NotNull LSFDrawRoot o) {
        addDeprecatedWarningAnnotation(o, "5.2", "6.0", "");
    }

    @Override
    public void visitNonEmptyClassParamDeclareList(@NotNull LSFNonEmptyClassParamDeclareList o) {
        super.visitNonEmptyClassParamDeclareList(o);

        java.util.List<LSFClassParamDeclare> params = o.getClassParamDeclareList();
        for (int i = 0; i < params.size(); i++) {
            LSFParamDeclare param1 = params.get(i).getParamDeclare();
            for (int j = i+1; j < params.size(); j++) {
                LSFParamDeclare param2 = params.get(j).getParamDeclare();
                if (BaseUtils.nullEquals(param1.getDeclName(), param2.getDeclName())) {
                    addAlreadyDefinedError(param2);
                    break;
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
    public void visitChangeInputPropertyCustomView(@NotNull LSFChangeInputPropertyCustomView changeInputPropertyCustomView) {
        super.visitChangeInputPropertyCustomView(changeInputPropertyCustomView);

        if (changeInputPropertyCustomView.getStringLiteral().getValue().isEmpty())
            addUnderscoredError(changeInputPropertyCustomView, "Wrong custom CHANGE function definition. 'changeFunction' can't be empty.");
    }

    @Override
    public void visitRenderPropertyCustomView(@NotNull LSFRenderPropertyCustomView renderPropertyCustomView) {
        super.visitRenderPropertyCustomView(renderPropertyCustomView);
        if (renderPropertyCustomView.getStringLiteral().getValue().isEmpty())
            addUnderscoredError(renderPropertyCustomView, "Wrong custom render function definition. 'renderFunction' can't be empty. Expected: CUSTOM 'renderFunction'");
    }

    @Override
    public void visitRoundPropertyDefinition(@NotNull LSFRoundPropertyDefinition o) {
        super.visitRoundPropertyDefinition(o);

        List<LSFPropertyExpression> expressionList = o.getPropertyExpressionList();

        LSFPropertyExpression prop = expressionList.get(0);
        LSFExClassSet propClass = prop.resolveValueClass(false);
        if(propClass != null && !(propClass.classSet instanceof IntegralClass)) {
            addUnderscoredError(prop, "ROUND is supported only for NUMERIC, INTEGER, LONG and DOUBLE");
        }

        if(expressionList.size() > 1) {
            expressionList.size();
            LSFPropertyExpression scaleProp = expressionList.get(1);
            LSFExClassSet scalePropClass = scaleProp.resolveValueClass(false);
            if (scalePropClass != null && !(scalePropClass.classSet instanceof IntegerClass)) {
                addUnderscoredError(scaleProp, "ROUND scale param should be INTEGER");
            }
        }
    }

    @Override
    public void visitChangePropertyCustomView(@NotNull LSFChangePropertyCustomView changePropertyCustomView) {
        super.visitChangePropertyCustomView(changePropertyCustomView);

        if (changePropertyCustomView.getStringLiteral().getValue().isEmpty())
            addUnderscoredError(changePropertyCustomView, "Wrong custom change function definition. 'changeFunction' can't be empty. Expected:" +
                    (changePropertyCustomView.getParent().getChildren().length > 1 ? "" : "CUSTOM ") + "CHANGE 'changeFunction'");
    }

    @Override
    public void visitJavaClassStringUsage(@NotNull LSFJavaClassStringUsage o) {
        super.visitJavaClassStringUsage(o);

        if(!isInMetaDecl(o)) {
            String elementText = o.getText();
            TextRange elementRange = o.getTextRange();

            for (PsiReference reference : o.getReferences()) {
                PsiElement resolved = reference.resolve();
                TextRange refRange = reference.getRangeInElement();
                TextRange refFileRange = TextRange.from(elementRange.getStartOffset() + refRange.getStartOffset(), refRange.getLength());
                if (resolved == null) {
                    addErrorAnnotation(o, refFileRange, "Can't resolve " + refRange.substring(elementText));
                } else if (refRange.getEndOffset() == elementRange.getLength() - 1) {
                    //последний компонент должен быть ActionProperty
                    boolean correctClass = resolved instanceof PsiClass && hasSuperClass((PsiClass) resolved, ACTION_FQN);
                    if (!correctClass) {
                        addErrorAnnotation(o, refFileRange, "Class " + elementText + " should extend Action");
                    }
                }
            }
        }
    }

    public void visitStringValueLiteral(@NotNull LSFStringValueLiteral o) {
        super.visitStringValueLiteral(o);
        if (!LSFStringUtils.isRawLiteral(o.getText())) {
            checkEscapeSequences(o, "nrt'\\");
        }
    }

    public void visitLocalizedStringValueLiteral(@NotNull LSFLocalizedStringValueLiteral o) {
        super.visitLocalizedStringValueLiteral(o);
        if (!o.isRawLiteral()) {
            checkEscapeSequences(o, "nrt'\\{}$");
            checkBracesConsistency(o);
            
            if (!o.needToBeLocalized()) {
                addLocalizationWarnings(o, o);
            }
        }
    }

    public void visitMetacodeStringValueLiteral(@NotNull LSFMetacodeStringValueLiteral o) {
        visitStringValueLiteral(o);
        if (!LSFStringUtils.isRawLiteral(o.getText())) {
            addLocalizationWarnings(o, o);
        }
    }

    private void addLocalizationWarnings(PsiElement element, LSFPropertiesFileValueGetter o) {
        Module module = ModuleUtil.findModuleForPsiElement(element);
        LSFResourceBundleUtils.ScopeData scopeData = LSFResourceBundleUtils.getScopeData(module, false);
        if(scopeData != null) {
            Map<String, Map<String, PropertiesFile>> propertiesFilesMap = scopeData.propertiesFiles;
            String currentLang = LSFResourceBundleUtils.getLsfStrLiteralsLanguage(module, false);
            if (currentLang != null) {

                List<IntentionAction> fixes = new ArrayList<>();

                for (Map.Entry<String, Map<String, PropertiesFile>> resourceBundleEntry : propertiesFilesMap.entrySet()) {

                    String resourceBundleName = resourceBundleEntry.getKey();
                    Map<String, PropertiesFile> propertiesFiles = resourceBundleEntry.getValue();

                    PropertiesFile currentPropertiesFile = propertiesFiles.get(currentLang);
                    String currentKey = currentPropertiesFile != null ? LSFResourceBundleUtils.getReverseMapValue(currentPropertiesFile.getVirtualFile().getPath(), o.getPropertiesFileValue().trim()) : null;

                    List<PropertiesFile> allPropertiesFiles = new ArrayList<>(propertiesFiles.values());
                    List<PropertiesFile> existingPropertiesFiles = new ArrayList<>();

                    //search current key in each properties file
                    for (PropertiesFile propFile : propertiesFiles.values()) {
                        boolean contains = LSFResourceBundleUtils.getOrdinaryMapValue(propFile.getVirtualFile().getPath(), currentKey) != null;
                        if (contains) {
                            existingPropertiesFiles.add(propFile);
                        }
                    }

                    if (existingPropertiesFiles.isEmpty()) {
                        fixes.add(getCreatePropertyFix(currentLang, resourceBundleName, allPropertiesFiles, LSFResourceBundleUtils.getDefaultBundleKey(o.getPropertiesFileValue()), element, o));
                    } else if (existingPropertiesFiles.size() < propertiesFiles.size()) {
                        fixes.clear();
                        fixes.add(getCreatePropertyFix(currentLang, resourceBundleName, allPropertiesFiles.stream().filter(f -> !existingPropertiesFiles.contains(f)).collect(Collectors.toList()), currentKey, element, o));
                        break;
                    } else {
                        fixes.clear();
                        break;
                    }
                }

                if (!fixes.isEmpty()) {
                    addWarningResourceBundleAnnotation(element, fixes);
                }
            }
        }
    }

    public void visitExpressionStringValueLiteral(@NotNull LSFExpressionStringValueLiteral o) {
        super.visitExpressionStringValueLiteral(o);
        visitLocalizedStringValueLiteral(o);
    }

    private IntentionAction getCreatePropertyFix(String currentLang, String resourceBundleName, List<PropertiesFile> propertiesFiles, String defaultKey, PsiElement element, LSFPropertiesFileValueGetter o) {
        return new IntentionAction() {
            @Override
            public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
                String defaultValue = o.getPropertiesFileValue();
                CreatePropertyFixDialog dialog = new CreatePropertyFixDialog(project, currentLang, propertiesFiles, defaultKey, defaultValue);
                if (dialog.showAndGet()) {
                    Collection<PropertiesFile> selectedPropertiesFiles = Collections.singletonList(dialog.getPropertiesFilesField());
                    String key = dialog.getKey();
                    String value = dialog.getValue();

                    for (PropertiesFile selectedFile : selectedPropertiesFiles) {
                        if (!FileModificationService.getInstance().prepareFileForWrite(selectedFile.getContainingFile()))
                            return;
                    }
                    UndoUtil.markPsiFileForUndo(file);

                    ApplicationManager.getApplication().runWriteAction(() -> CommandProcessor.getInstance().executeCommand(project, () -> I18nUtil.createProperty(project, selectedPropertiesFiles, key, value), "I18n", project));

                    element.accept(LSFReferenceAnnotator.this);
                }
            }

            @Override
            public @IntentionName
            @NotNull
            String getText() {
                return "Create property for " + element.getText() + " in " + resourceBundleName;
            }

            @Override
            public @NotNull
            @IntentionFamilyName String getFamilyName() {
                return getText();
            }

            @Override
            public boolean isAvailable(@NotNull Project project, Editor editor, PsiFile file) {
                return true;
            }

            @Override
            public boolean startInWriteAction() {
                return false;
            }
        };
    }

    private void checkEscapeSequences(PsiElement element, final String escapedSymbols) {
        String text = element.getText();
        for (int i = 0; i < text.length(); ++i) {
            char curCh = text.charAt(i);
            if (curCh == '\\') {
                if (i + 1 < text.length() && !escapedSymbols.contains(text.substring(i + 1, i + 2))) {
                    addUnderscoredError(element, createRange(element, i, 2), "Wrong escape sequence " + text.substring(i, i + 2));
                } else {
                    ++i;
                }
            }
        }
    }

    private void checkBracesConsistency(PsiElement element) {
        String str = element.getText();
        Stack<Integer> opened = new Stack<>();
        for (int i = 0; i < str.length(); ++i) {
            char ch = str.charAt(i);
            if (ch == '\\') {
                ++i;
            } else if (ch == '{') {
                opened.push(i);
            } else if (ch == '}') {
                if (opened.empty()) {
                    addUnderscoredError(element, createRange(element, i, 1), "'{' is missing");
                } else {
                    opened.pop();
                }
            }
        }
        if (!opened.empty()) {
            addUnderscoredError(element, createRange(element, opened.peek(), 1), "'}' is missing");
        }
    }

    private TextRange createRange(PsiElement element, int pos, int len) {
        int start = element.getTextRange().getStartOffset();
        return TextRange.create(start + pos, start + pos + len);
    }

    private boolean checkReference(LSFReference reference) {
        LSFResolvingError errorAnnotation = reference.resolveErrorAnnotation(myHolder);
        if (errorAnnotation != null) { // !isInMetaDecl(reference)
            if(errorAnnotation.deprecated)
                addDeprecatedWarningAnnotation(reference, errorAnnotation.text);
            else
                addErrorWithResolving(reference, errorAnnotation); // since in meta usage there can be total different resolved references
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
                addUnderscoredErrorWithResolving(relationalPE, String.format("Type mismatch: can't compare %s and %s", class1, class2)); // since in meta usage there can be total different resolved references
            }
        }
    }
    
    private void checkPostfixPE(LSFPostfixUnaryPE relationalPE) {
        LSFTypePropertyDefinition type = relationalPE.getTypePropertyDefinition();
        if (type != null && relationalPE.getChildren().length == 2) {
            LSFClassSet class1 = getLSFClassSet(relationalPE.getSimplePE());
            LSFClassSet class2 = LSFPsiImplUtil.resolveClass(type.getClassName());
            if (class1 != null && class2 != null && !class1.haveCommonChildren(class2, null)) {
                addWarningAnnotation(relationalPE, String.format("Type mismatch: can't cast %s to %s", class1, class2));
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
                addUnderscoredErrorWithResolving(equalityPE, String.format("Type mismatch: can't compare %s and %s", class1, class2)); // since in meta usage there can be total different resolved references
            }
        }
    }

    private LSFClassSet getLSFClassSet(LSFRelationalPE element) {
        return LSFExClassSet.fromEx(element.resolveInferredValueClass(null));
    }

    private LSFClassSet getLSFClassSet(LSFSimplePE element) {
        return LSFExClassSet.fromEx(element.resolveInferredValueClass(null));
    }

    private void checkAlreadyDefined(LSFDeclaration declaration) {
        if (declaration.getName() != null && declaration.resolveDuplicates()) {
            addAlreadyDefinedError(declaration);
        }
    }

    private void addDuplicateColumnNameError(PsiElement element, String tableName, String columnName) {
        addUnderscoredErrorWithResolving(element, "The property has duplicate column name. Table: " + tableName + ", column: " + columnName);
    }

    //error annotations

    private void addAlreadyDefinedError(LSFDeclaration decl) {
        PsiElement element = decl.getDuplicateElement();
        addUnderscoredErrorWithResolving(element, element.getTextRange(), "'" + decl.getPresentableText() + "' is already defined");
    }

    private void addHighlightErrorWithResolving(PsiElement element, String message) {
        addErrorWithResolving(element, new LSFResolvingError(element, message, false));
    }
    private void addUnderscoredErrorWithResolving(PsiElement element, TextRange range, String message) {
        addErrorWithResolving(element, new LSFResolvingError(element, range, message, true));
    }
    private void addUnderscoredErrorWithResolving(PsiElement element, String message) {
        addErrorWithResolving(element, new LSFResolvingError(element, message, true));
    }
    private void addErrorWithResolving(PsiElement element, LSFResolvingError error) {
        addError(element, error, true);
    }
    private void addUnderscoredError(PsiElement element, TextRange range, String message) {
        addError(element, new LSFResolvingError(element, range, message, true), false);
    }
    private void addUnderscoredError(PsiElement element, String message) {
        addError(element, new LSFResolvingError(element, message, true), false);
    }
    private void addError(PsiElement element, LSFResolvingError error, boolean hasResolving) {
        if (isInMetaDecl(element) && hasResolving) {
            addInfoAnnotation(element, error.range, error.text, error.underscored ? WAVE_UNDERSCORED_META_ERROR : META_ERROR);
        } else {
            addErrorAnnotation(element, error.range, error.text, error.underscored ? WAVE_UNDERSCORED_ERROR : ERROR);
        }
    }

    private void addErrorAnnotation(PsiElement element, TextRange range, String text) {
        addErrorAnnotation(element, range, text, ERROR);
    }

    private void addErrorAnnotation(PsiElement element, TextRange range, String text, TextAttributes textAttributes) {
        if (errorsSearchMode) {
            ShowErrorsAction.showErrorMessage(element, text, LSFErrorLevel.ERROR);
        } else {
            AnnotationBuilder annotationBuilder = myHolder.newAnnotation(HighlightSeverity.ERROR, text);
            if (range != null) {
                annotationBuilder = annotationBuilder.range(range);
            }
            annotationBuilder.enforcedTextAttributes(mergeMetaAttributes(element, textAttributes))
                    .create();
        }
    }

    //warning annotations

    private void addWarningResourceBundleAnnotation(PsiElement element, List<IntentionAction> fixes) {
        addWarningAnnotation(element, "Missing " + element.getText() + " in resource bundle", WAVE_UNDERSCORED_WARNING, fixes);
    }

    private void addDeprecatedWarningAnnotation(PsiElement element, String deprecatedVersion, String comment) {
        addDeprecatedWarningAnnotation(element, String.format("Deprecated since version %s. %s", deprecatedVersion, comment));
    }

    private void addDeprecatedWarningAnnotation(PsiElement element, String deprecatedVersion, String removedVersion, String comment) {
        addDeprecatedWarningAnnotation(element, String.format("Deprecated since version %s, removed in version %s. %s", deprecatedVersion, removedVersion, comment));
    }

    private void addDeprecatedWarningAnnotation(PsiElement element, String text) {
        addWarningAnnotation(element, text, DEPRECATED_WARNING);
    }

    private void addWarningAnnotation(PsiElement element, String text) {
        addWarningAnnotation(element, text, null);
    }

    private void addWarningAnnotation(PsiElement element, String text, TextAttributes textAttributes) {
        addWarningAnnotation(element, text, textAttributes, null);
    }

    private void addWarningAnnotation(PsiElement element, String text, TextAttributes textAttributes, List<IntentionAction> fixes) {
        if(warningsSearchMode) {
            ShowErrorsAction.showErrorMessage(element, text, LSFErrorLevel.WARNING);
        } else if(!errorsSearchMode) {
            AnnotationBuilder builder = myHolder.newAnnotation(HighlightSeverity.WARNING, text).range(element);
            if(textAttributes != null) {
                builder = builder.enforcedTextAttributes(textAttributes);
            }
            if(fixes != null) {
                for (IntentionAction fix : fixes) {
                    builder = builder.withFix(fix);
                }
            }
            builder.create();
        }
    }

    //info annotations

    private void addInfoAnnotation(PsiElement element, String text, TextAttributes textAttributes) {
        addInfoAnnotation(element, element.getTextRange(), text, textAttributes);
    }

    private void addInfoAnnotation(PsiElement element, TextRange range, String text, TextAttributes textAttributes) {
        if(!errorsSearchMode) {
            AnnotationBuilder builder = myHolder.newAnnotation(HighlightSeverity.INFORMATION, text);
            if(range != null) {
                builder = builder.range(range);
            }
            builder.enforcedTextAttributes(mergeMetaAttributes(element, textAttributes)).create();
        }
    }

    private void addSilentInfoAnnotation(PsiElement element, TextAttributes textAttributes) {
        if(!errorsSearchMode) {
            myHolder.newSilentAnnotation(HighlightSeverity.INFORMATION)
                    .range(element.getTextRange())
                    .enforcedTextAttributes(textAttributes)
                    .create();
        }
    }

    private TextAttributes mergeMetaAttributes(PsiElement element, TextAttributes attributes) {
        boolean inMetaUsage = isInMetaUsage(element);
        boolean inMetaDecl = isInMetaDecl(element);
        TextAttributes metaAttr;
        if (inMetaUsage) {
            if(inMetaDecl)
                metaAttr = META_DECL_USAGE;
            else
                metaAttr = META_USAGE;
        } else {
            if (inMetaDecl)
                metaAttr = META_DECL;
            else
                metaAttr = null;
        }

        if(metaAttr != null) {
            if (attributes != null)
                attributes = TextAttributes.merge(attributes, metaAttr);
            else
                attributes = metaAttr;
        }
        return attributes;
    }

    private void addIndirectProp(PsiElement element) {
        addInfoAnnotation(element, "Indirect usage", IMPLICIT_DECL);
    }

    private void addImplicitDecl(PsiElement element) {
        addInfoAnnotation(element, "Implicit parameter declaration", IMPLICIT_DECL);
    }

    private void addUntypedImplicitDecl(PsiElement element) {
        addInfoAnnotation(element, "Untyped implicit parameter declaration", UNTYPED_IMPLICIT_DECL);
    }

    @Override
    public void visitOverridePropertyStatement(@NotNull LSFOverridePropertyStatement element) {
        Result<LSFClassSet> required = new Result<>();
        Result<LSFClassSet> found = new Result<>();
        if (!LSFPsiImplUtil.checkOverrideValue(element, required, found)) {
            String requiredClass = required.getResult() instanceof LSFValueClass ? ((LSFValueClass) required.getResult()).getCaption() : required.getResult().getCanonicalName();
            String foundClass = found.getResult() instanceof LSFValueClass ? ((LSFValueClass) found.getResult()).getCaption() : found.getResult().getCanonicalName();
            addUnderscoredErrorWithResolving(element, "Wrong value class. Required: " + requiredClass + ", found: " + foundClass);
        }

        if (!LSFPsiImplUtil.checkNonRecursiveOverride(element)) {
            addUnderscoredErrorWithResolving(element, "Recursive implement");
        }
    }
    @Override
    public void visitOverrideActionStatement(@NotNull LSFOverrideActionStatement element) {
        if (!LSFPsiImplUtil.checkNonRecursiveOverride(element)) {
            addUnderscoredErrorWithResolving(element, "Recursive implement");
        }
    }


    @Override
    public void visitAssignActionPropertyDefinitionBody(@NotNull LSFAssignActionPropertyDefinitionBody o) {
        LSFPropertyUsageImpl propertyUsage = (LSFPropertyUsageImpl) o.getFirstChild().getFirstChild();
        if (propertyUsage != null) {
            LSFPropDeclaration declaration = propertyUsage.resolveDecl();
            if (declaration != null) {
                if (declaration instanceof LSFPropertyStatement) {
                    LSFPropertyCalcStatement propertyStatement = ((LSFPropertyStatement) declaration).getPropertyCalcStatement();
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

                    checkTypeMismatch(o, declaration);

                } else if (declaration instanceof LSFLocalPropertyDeclarationNameImpl) {
                    checkTypeMismatch(o, declaration);
                }
            }
        }
    }

    private void checkTypeMismatch(LSFAssignActionPropertyDefinitionBody o, LSFPropDeclaration declaration) {
        LSFClassSet leftClass = declaration.resolveValueClass();
        List<LSFPropertyExpression> rightPropertyExpressionList = o.getPropertyExpressionList();
        if (!rightPropertyExpressionList.isEmpty()) {
            LSFPropertyExpression rightPropertyExpression = rightPropertyExpressionList.get(0);
            LSFClassSet rightClass = LSFExClassSet.fromEx(rightPropertyExpression.resolveValueClass(false));
            if (leftClass != null && rightClass != null) {
                if (!leftClass.isCompatible(rightClass)) {
                    addTypeMismatchError(o, rightClass, leftClass);
                } else if (leftClass.getCanonicalName().equals("BOOLEAN") && rightPropertyExpression.getText().equals("FALSE")) {
                    addFalseToBooleanAssignError(o);
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

    private void addFalseToBooleanAssignError(LSFAssignActionPropertyDefinitionBody o) {
        addUnderscoredError(o, "use NULL instead of FALSE");
    }

    private void addAssignError(LSFAssignActionPropertyDefinitionBody o) {
        addUnderscoredErrorWithResolving(o, "ASSIGN is allowed only to DATA/MULTI/CASE property"); // uses resolving
    }

    private void addTypeMismatchError(LSFAssignActionPropertyDefinitionBody o, LSFClassSet class1, LSFClassSet class2) {
        addUnderscoredErrorWithResolving(o, String.format("Type mismatch: can't cast %s to %s", class1.getCanonicalName(), class2.getCanonicalName())); // uses resolving
    }

    @Override
    public void visitPropertyCustomView(@NotNull LSFPropertyCustomView o) {
        LSFStringLiteral selectType = o.getStringLiteral();
        if(selectType != null) {
            checkSelect(o, selectType.getText());
        }
    }

    @Override
    public void visitSetObjectPropertyStatement(@NotNull LSFSetObjectPropertyStatement o) {
        String text = o.getText();
        if (text != null && text.contains("=")) {
            String property = text.substring(0, text.indexOf("=")).trim();

            LSFComponentPropertyValue element = o.getComponentPropertyValue();
            Object valueClass = null;
            if (element != null && !isInMetaDecl(element)) {
                if (element.getBooleanLiteral() != null) {
                    valueClass = LogicalClass.instance;
                } else if (element.getContainerTypeLiteral() != null) {
                    valueClass = element.getContainerTypeLiteral();
                } else if (element.getDesignCalcPropertyObject() != null) {
                    LSFDesignCalcPropertyObject calcValue = element.getDesignCalcPropertyObject();
                    if (calcValue != null) {
                        LSFPropertyExpression propertyExpression = calcValue.getFormCalcPropertyObject().getFormExprDeclaration().getPropertyExpression();
                        LSFExClassSet classSet = LSFPsiImplUtil.resolveInferredValueClass(propertyExpression, null);
                        if (classSet != null) {
                            valueClass = classSet.classSet;
                        }
                    }
                } else if (element.getDimensionLiteral() != null) {
                    valueClass = element.getDimensionLiteral();
                } else if (element.getFlexAlignmentLiteral() != null) {
                    valueClass = element.getFlexAlignmentLiteral();
                } else if (element.getTbooleanLiteral() != null) {
                    valueClass = LogicalClass.threeStateInstance;
                }

                Class<?> cls = ASTCompletionContributor.DESIGN_PROPERTIES.get(property);
                if (cls == null) {
                    addHighlightErrorWithResolving(o, "Can't resolve property " + property); // design property can be meta parameter
                } else if(valueClass != null) {
                    Class<?> mismatchClass = null;
                    if ((cls == Integer.class || cls == int.class) && !(valueClass instanceof IntegerClass)) {
                        mismatchClass = cls;
                    } else if ((cls == Double.class || cls == double.class) && !(valueClass instanceof IntegerClass) && !(valueClass instanceof DoubleClass)) {
                        mismatchClass = cls;
                    } else if (cls == String.class && !(valueClass instanceof AStringClass)) {
                        mismatchClass = cls;
                    } else if (cls == Color.class && !(valueClass instanceof ColorClass)) {
                        mismatchClass = cls;
                    } else if ((cls == FontInfo.class || cls == KeyStroke.class) && !(valueClass instanceof StringClass)) {
                        mismatchClass = StringClass.class;
                    } else if (cls == Dimension.class && !(valueClass instanceof LSFDimensionLiteral)) {
                        mismatchClass = Dimension.class;
                    } else if (cls == FlexAlignment.class && !(valueClass instanceof LSFFlexAlignmentLiteral)) {
                        mismatchClass = FlexAlignment.class;
                    } else if (cls == ContainerType.class && !(valueClass instanceof LSFContainerTypeLiteral)) {
                        mismatchClass = ContainerType.class; //deprecated in 5.2, removed in 6.0
                    }
                    if (mismatchClass != null) {
                        addUnderscoredError(element, String.format("Type mismatch: can't cast %s to %s", valueClass, mismatchClass));
                    }
                }
            }

            if (property.equals("columns")) {
                addDeprecatedWarningAnnotation(o, "5.2", "6.0", "Use 'lines' instead");
            } else if (property.equals("type")) {
                addDeprecatedWarningAnnotation(o, "5.2", "6.0", "Use 'horizontal', 'tabbed', 'lines' instead");
            } else if (property.equals("autoSize")) {
                addDeprecatedWarningAnnotation(o, "6.0", "Earlier versions: ignore this warning");
            } else if (property.equals("toolTip")) {
                addDeprecatedWarningAnnotation(o, "5.2", "6.0", "Use 'tooltip' instead");
            } else if (property.equals("editOnSingleClick")) {
                addDeprecatedWarningAnnotation(o, "5.2", "6.0", "Use 'changeOnSingleClick' instead");
            } else if (property.equals("valueAlignment")) {
                addDeprecatedWarningAnnotation(o, "6.0", "Use 'valueAlignmentHorz' instead");
            } else if (property.equals("showGroup")) {
                addDeprecatedWarningAnnotation(o, "6.0", "Use 'showViews' instead");
            } else if (property.equals("changeKeyPriority")) {
                addDeprecatedWarningAnnotation(o, "6.0", "Use parameter 'priority' in 'changeKey' instead");
            } else if (property.equals("changeMousePriority")) {
                addDeprecatedWarningAnnotation(o, "6.0", "Use parameter 'priority' in 'changeMouse' instead");
            } else if (element != null && !element.getText().equals("NULL")) {
                switch (property) {
                    case "fontStyle":
                        checkFontStyle(element, element.getText());
                        break;
                    case "select":
                        checkSelect(element, element.getText());
                        break;
                    case "defaultCompare":
                        checkDefaultCompare(element, element.getText());
                        break;
                }
            }
        }
    }

    private static List<String> allowedFontStyles = Arrays.asList("bold", "italic");
    private void checkFontStyle(LSFComponentPropertyValue element, String fontStyle) {
        for (String style : LSFStringUtils.unquote(fontStyle).split(" ")) {
            if (!allowedFontStyles.contains(style)) {
                addUnderscoredError(element, "fontStyle value must be a combination of strings bold and italic");
                break;
            }
        }
    }

    private List<String> allowedSelects = Arrays.asList("button", "buttonGroup", "dropdown", "input", "list");
    private void checkSelect(PsiElement element, String select) {
        if (!allowedSelects.contains(LSFStringUtils.unquote(select))) {
            addUnderscoredError(element, "select value must be one of these: " + allowedSelects);
        }
    }

    private static List<String> allowedDefaultCompares = Arrays.asList("=", ">", "<", ">=", "<=", "!=", "=*", "=@");
    private static List<String> allowedDefaultCompares5 = Arrays.asList("EQUALS", "GREATER", "LESS", "GREATER_EQUALS", "LESS_EQUALS", "NOT_EQUALS", "LIKE", "CONTAINS");
    private void checkDefaultCompare(LSFComponentPropertyValue element, String defaultCompare) {
        defaultCompare = LSFStringUtils.unquote(defaultCompare);
        if (!allowedDefaultCompares.contains(defaultCompare)) {
            if(!allowedDefaultCompares5.contains(defaultCompare)) {
                addUnderscoredError(element, "defaultCompare value must be one of: " + allowedDefaultCompares);
            } else {
                addDeprecatedWarningAnnotation(element, "5.2", "6.0", "defaultCompare value must be one of: " + allowedDefaultCompares);
            }
        }
    }

    @Override
    public void visitTypeMult(@NotNull LSFTypeMult o) {
        for (PsiElement child : o.getParent().getChildren()) {
            if (child instanceof LSFUnaryMinusPE) {
                LSFClassSet classSet = LSFExClassSet.fromEx(((LSFUnaryMinusPE) child).resolveInferredValueClass(null));
                if (classSet != null && !(classSet instanceof IntegralClass)) {
                    addUnderscoredErrorWithResolving(child, "Can't multiply / divide " + classSet + " value"); // uses resolving
                }
            }
        }
    }

    @Override
    public void visitWindowType(@NotNull LSFWindowType o) {
        if (!o.getText().equals("NATIVE")) {
            addDeprecatedWarningAnnotation(o, "5.2", "6.0", "Ignore until 6.0");
        }
    }

    @Override
    public void visitAdditiveORPE(@NotNull LSFAdditiveORPE o) {
        List<LSFAdditivePE> additivePEList = o.getAdditivePEList();
        if (additivePEList.size() > 1) {
            for (LSFAdditivePE child : additivePEList) {
                LSFClassSet classSet = LSFExClassSet.fromEx(child.resolveInferredValueClass(null));
                if (classSet != null && !(classSet instanceof IntegralClass)) {
                    addUnderscoredErrorWithResolving(child, "Can't add / subtract " + classSet + " value"); // uses resolving
                }
            }
        }
    }

    @Override
    public void visitModuleUsage(@NotNull LSFModuleUsage moduleUsage) {
        checkReference(moduleUsage);
    }

    @Override
    public void visitJoinPropertyDefinition(@NotNull LSFJoinPropertyDefinition o) {
        LSFPropertyExprObject propertyExprObject = o.getPropertyExprObject();
        if(propertyExprObject != null) {
            LSFPropertyCalcStatement propertyCalcStatement = propertyExprObject.getPropertyCalcStatement();
            if (propertyCalcStatement != null) {
                LSFExpressionUnfriendlyPD expressionUnfriendlyPD = propertyCalcStatement.getExpressionUnfriendlyPD();
                if (expressionUnfriendlyPD != null) {
                    LSFGroupPropertyDefinition groupPropertyDefinition = expressionUnfriendlyPD.getGroupPropertyDefinition();
                    if (groupPropertyDefinition != null) {
                        LSFNonEmptyPropertyExpressionList leftPropertyExpressionList = groupPropertyDefinition.getGroupPropertyBy().getNonEmptyPropertyExpressionList();
                        if (leftPropertyExpressionList != null) {
                            List<LSFClassSet> leftClassList = new ArrayList<>();
                            List<LSFClassSet> rightClassList = new ArrayList<>();

                            for (LSFPropertyExpression propertyExpression : leftPropertyExpressionList.getPropertyExpressionList()) {
                                leftClassList.add(LSFExClassSet.fromEx(propertyExpression.resolveValueClass(false)));
                            }

                            LSFPropertyExpressionList propertyExpressionList = o.getPropertyExpressionList();
                            if (propertyExpressionList != null) {
                                LSFNonEmptyPropertyExpressionList rightPropertyExpressionList = propertyExpressionList.getNonEmptyPropertyExpressionList();
                                if (rightPropertyExpressionList != null) {
                                    for(LSFPropertyExpression propertyExpression : rightPropertyExpressionList.getPropertyExpressionList()) {
                                        rightClassList.add(LSFExClassSet.fromEx(propertyExpression.resolveValueClass(false)));
                                    }
                                }
                            }

                            if(!isCompatible(leftClassList, rightClassList)) {
                                addTypeMismatchError(o, leftClassList, rightClassList);
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isCompatible(List<LSFClassSet> leftClassList, List<LSFClassSet> rightClassList) {
        boolean compatible = leftClassList.size() == rightClassList.size();
        if(compatible) {
            for (int i = 0; i < leftClassList.size(); i++) {
                LSFClassSet leftClass = leftClassList.get(i);
                LSFClassSet rightClass = rightClassList.get(i);
                if (leftClass != null && rightClass != null && !leftClass.isCompatible(rightClass)) {
                    compatible = false;
                    break;
                }
            }
        }
        return compatible;
    }

    private void addTypeMismatchError(LSFJoinPropertyDefinition o, List<LSFClassSet> leftClassList, List<LSFClassSet> rightClassList) {
        addUnderscoredErrorWithResolving(o, "Type mismatch. Expected params: " +
                leftClassList.stream().map(lsfClassSet -> lsfClassSet != null ? lsfClassSet.getCanonicalName() : null).collect(Collectors.joining(", ")) + "; got: " +
                rightClassList.stream().map(lsfClassSet -> lsfClassSet != null ? lsfClassSet.getCanonicalName() : null).collect(Collectors.joining(", "))); // uses resolving
    }

    @Override
    public void visitInputActionPropertyDefinitionBody(@NotNull LSFInputActionPropertyDefinitionBody o) {
        // Many if-blocks and null checks is needed to add an error in the right place
        LSFListWhereInputProps listWhereInputProps = o.getListWhereInputProps();
        if (listWhereInputProps != null) {
            LSFListInputProp listInputProp = listWhereInputProps.getListInputProp();
            if (listInputProp != null) {
                if (listInputProp.getListActionDefinitionBody() != null) {
                    LSFParamDeclare paramDeclare = o.getParamDeclare();
                    if (paramDeclare != null) {
                        LSFClassSet classSet = paramDeclare.resolveClass();
                        if (classSet != null && !(classSet instanceof DataClass)) {
                            LSFClassOrExpression nextSiblingOfType = PsiTreeUtil.getNextSiblingOfType(paramDeclare, LSFClassOrExpression.class);
                            if (nextSiblingOfType != null)
                                addUnderscoredError(paramDeclare, nextSiblingOfType.getTextRange(), "Action in LIST can only be used for built-in types");
                        }
                    }
                }
            }
        }
    }

    @Override
    public void visitEditFormDeclaration(@NotNull LSFEditFormDeclaration o) {
        visitEditOrListFormDeclaration(o, o.getObjectUsage(), o.getCustomClassUsage());
    }

    @Override
    public void visitListFormDeclaration(@NotNull LSFListFormDeclaration o) {
        visitEditOrListFormDeclaration(o, o.getObjectUsage(), o.getCustomClassUsage());
    }

    private void visitEditOrListFormDeclaration(PsiElement o, LSFObjectUsage objectUsage, LSFCustomClassUsage customClassUsage) {
        if(objectUsage != null) {
            LSFObjectDeclaration objectUsageDecl = objectUsage.resolveDecl();
            if(objectUsageDecl != null) {
                LSFClassSet leftClass = LSFPsiImplUtil.resolveClass(customClassUsage);
                LSFClassSet rightClass = objectUsageDecl.resolveClass();
                if (leftClass != null && rightClass != null && !leftClass.haveCommonChildren(rightClass, null)) {
                    addWarningAnnotation(o, String.format("Type mismatch: can't cast %s to %s", leftClass, rightClass));
                }
            }
        }
    }

    public static List<String> supportedMapTileProviders = Arrays.asList("openStreetMap", "google", "yandex");
    @Override
    public void visitMapOptions(@NotNull LSFMapOptions o) {
        super.visitMapOptions(o);
        String option = o.getFirstChild().getText();
        if (!supportedMapTileProviders.contains(LSFStringUtils.unquote(option))) {
            addUnderscoredError(o, format("'%s' is not supported map option definition, use on of: %s", option, supportedMapTileProviders));
        }
    }

    @Override
    public void visitJsStringUsage(@NotNull LSFJsStringUsage o) {
        if (o.getParent().getChildren().length > 1) {
            String text = o.getFirstChild().getText();
            PsiElement lastChild = o.getParent().getLastChild();
            if (lastChild.getText().length() > 2 && (text.contains(".js") || text.contains(".css")))
                addUnderscoredError(lastChild, "Calling '.js' or '.css' file should not have arguments");
        }
        super.visitJsStringUsage(o);
    }

    @Override
    public void visitPredefinedFormPropertyName(@NotNull LSFPredefinedFormPropertyName o) {
        @Nullable LSFPredefinedAddPropertyName predefinedAdd = o.getPredefinedAddPropertyName();
        @Nullable LSFExplicitPropClass explicitPropClass = o.getExplicitPropClass();
        if (predefinedAdd != null && !isInMetaDecl(predefinedAdd)) {
            if (predefinedAdd.getPredefinedNewPropertyName() != null && (explicitPropClass == null || isAbstractClass(LSFPsiImplUtil.resolveClass(explicitPropClass.getClassName())))) {
                @Nullable LSFFormMappedNamePropertiesList formMappedNamePropertiesList = PsiTreeUtil.getParentOfType(o, LSFFormMappedNamePropertiesList.class);
                if (formMappedNamePropertiesList != null) {
                    @Nullable LSFObjectUsageList objectUsageList = formMappedNamePropertiesList.getObjectUsageList();
                    if (objectUsageList != null) {
                        LSFNonEmptyObjectUsageList nonEmptyObjectUsageList = objectUsageList.getNonEmptyObjectUsageList();
                        if (nonEmptyObjectUsageList != null) {
                            for (LSFObjectUsage objectUsage : nonEmptyObjectUsageList.getObjectUsageList()) {
                                if (isAbstractClass(objectUsage.resolveClass())) {
                                    addUnderscoredError(o, o.getText() + " for ABSTRACT class is not supported");
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private boolean isAbstractClass(LSFClassSet classSet) {
        if (classSet instanceof CustomClassSet) {
            Set<LSFClassDeclaration> classes = ((CustomClassSet) classSet).getClasses();
            if (classes.size() == 1) {
                LSFClassDeclaration cls = classes.iterator().next();
                if (cls instanceof LSFClassDeclImpl && ((LSFClassDeclImpl) cls).getAbstractLiteral() != null) {
                    return true;
                }
            }
        }
        return false;
    }
}
