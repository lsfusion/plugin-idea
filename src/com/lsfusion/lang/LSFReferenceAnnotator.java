package com.lsfusion.lang;

import com.intellij.codeInsight.FileModificationService;
import com.intellij.codeInsight.intention.IntentionAction;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.codeInspection.util.IntentionName;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
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
import com.lsfusion.lang.classes.CustomClassSet;
import com.lsfusion.lang.classes.IntegralClass;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.meta.MetaNestingLineMarkerProvider;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.ExprsContextModifier;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFExtend;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.impl.LSFLocalPropertyDeclarationNameImpl;
import com.lsfusion.lang.psi.impl.LSFPropertyUsageImpl;
import com.lsfusion.lang.psi.references.*;
import com.lsfusion.lang.typeinfer.LSFExClassSet;
import com.lsfusion.util.BaseUtils;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

import static com.lsfusion.util.JavaPsiUtils.hasSuperClass;

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
        if (o instanceof LeafPsiElement) { // фокус в том что побеждает наибольший приоритет, но важно следить что у верхнего правила всегда приоритет выше, так как в противном случае annotator просто херится
            TextAttributes textAttributes = mergeMetaAttributes(o, null);
            if(textAttributes != null) {
                Annotation annotation = myHolder.createInfoAnnotation(o.getTextRange(), null);
                annotation.setEnforcedTextAttributes(textAttributes);
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
            if (parentRef == null || o != parentRef) {
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

    private void addOuterRef(LSFReference reference) {
        final Annotation annotation = myHolder.createInfoAnnotation(reference.getTextRange(), "Outer param");
        TextAttributes error = OUTER_PARAM;
        error = mergeMetaAttributes(reference, error);
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

    public static LSFMetaCodeDeclarationStatement getMetaDecl(PsiElement o) {
        return PsiTreeUtil.getParentOfType(o, LSFMetaCodeDeclarationStatement.class);
    }

    public static boolean isInMetaDecl(PsiElement o) {
        return getMetaDecl(o) != null;
        //&& PsiTreeUtil.getParentOfType(o, LSFMetaCodeStatement.class) == null
    }

/*
    @Override
    public void visitMetaDeclaration(@NotNull LSFMetaDeclaration o) {
        super.visitMetaDeclaration(o);

        Annotation annotation = myHolder.createWarningAnnotation(o.getTextRange(), "Infer type");
        annotation.registerFix(new MetaTypeInferAction(o));
    }*/

    private void checkMetaNestingUsage(@NotNull PsiElement o) {
        if (MetaNestingLineMarkerProvider.resolveNestingLevel(o) > 1) {
            Annotation metaHeaderAnnotation = myHolder.createInfoAnnotation(o.getTextRange(), "");
            metaHeaderAnnotation.setEnforcedTextAttributes(META_NESTING_USAGE);
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
        
//        LSFMetaCodeDeclBody statements = o.getMetaCodeDeclBody();
//        if (statements != null) {
//            Annotation annotation = myHolder.createInfoAnnotation(statements.getTextRange(), "");
//            annotation.setEnforcedTextAttributes(META_DECL);
//        }
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
            } else {
//                    addColumnInfo(o.getNameIdentifier(), o.getTableName(), o.getColumnName());
            }
        }

        LSFPropertyDeclParams propertyDeclParams = o.getPropertyDeclaration().getPropertyDeclParams();
        if(propertyDeclParams != null) {
            LSFPropertyCalcStatement propertyCalcStatement = o.getPropertyCalcStatement();
            LSFExpressionUnfriendlyPD expressionUnfriendlyPD = propertyCalcStatement.getExpressionUnfriendlyPD();
            if (expressionUnfriendlyPD != null) {
                List<LSFParamDeclaration> declareParams = LSFPsiImplUtil.resolveParams(propertyDeclParams.getClassParamDeclareList());
                Pair<List<LSFParamDeclaration>, Map<PsiElement, Pair<LSFClassSet, LSFClassSet>>> incorrect = expressionUnfriendlyPD.checkValueParamClasses(declareParams);

                for (LSFParamDeclaration incParam : incorrect.first) {
                    addUnderscoredError(incParam, "Not used / No implementation found");
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
                        final Annotation annotation = myHolder.createWarningAnnotation(declareParam, "Parameter is not used");
                        TextAttributes error = WARNING;
                        error = mergeMetaAttributes(declareParam, error);
                        annotation.setEnforcedTextAttributes(error);
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
    public void visitFormDecl(@NotNull LSFFormDecl o) {
        super.visitFormDecl(o);

        checkAlreadyDefined(((LSFFormExtend) o.getParent()));
    }

    @Override
    public void visitExtendingFormDeclaration(@NotNull LSFExtendingFormDeclaration o) {
        super.visitExtendingFormDeclaration(o);

        checkAlreadyDefined((LSFFormExtend) o.getParent());
    }

    @Override
    public void visitDesignStatement(@NotNull LSFDesignStatement o) {
        super.visitDesignStatement(o);

        checkAlreadyDefined(o);
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
    public void visitRenderPropertyCustomView(@NotNull LSFRenderPropertyCustomView renderPropertyCustomView) {
        super.visitRenderPropertyCustomView(renderPropertyCustomView);
        if (renderPropertyCustomView.getStringLiteral().getValue().isEmpty())
            addUnderscoredError(renderPropertyCustomView, "Wrong custom render function definition. 'renderFunction' can't be empty. Expected: CUSTOM RENDER 'renderFunction'");
    }

    @Override
    public void visitEditPropertyCustomView(@NotNull LSFEditPropertyCustomView editPropertyCustomView) {
        super.visitEditPropertyCustomView(editPropertyCustomView);

        if (editPropertyCustomView.getStringLiteral().getValue().isEmpty())
            addUnderscoredError(editPropertyCustomView, "Wrong custom edit function definition. 'editFunction' can't be empty. Expected:" +
                    (editPropertyCustomView.getParent().getChildren().length > 1 ? "" : "CUSTOM ") + "EDIT [TEXT / REPLACE] 'editFunction'");
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
                boolean correctClass = resolved instanceof PsiClass && hasSuperClass((PsiClass) resolved, ACTION_FQN);
                if (!correctClass) {
                    Annotation annotation = myHolder.createErrorAnnotation(refFileRange, "Class " + elementText + " should extend Action");
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

        if (!o.needToBeLocalized()) {
            Module module = ModuleUtil.findModuleForPsiElement(o);

            LSFResourceBundleUtils.ScopeData scopeData = LSFResourceBundleUtils.getScopeData(module);
            if(scopeData != null) {
                Map<String, Map<String, PropertiesFile>> propertiesFilesMap = scopeData.propertiesFiles;
                String currentLang = LSFResourceBundleUtils.getLsfStrLiteralsLanguage(module, false);
                if (currentLang != null) {

                    List<IntentionAction> fixes = new ArrayList<>();

                    for (Map.Entry<String, Map<String, PropertiesFile>> resourceBundleEntry : propertiesFilesMap.entrySet()) {

                        String resourceBundleName = resourceBundleEntry.getKey();
                        Map<String, PropertiesFile> propertiesFiles = resourceBundleEntry.getValue();

                        PropertiesFile currentPropertiesFile = propertiesFiles.get(currentLang);
                        String currentKey = currentPropertiesFile != null ? LSFResourceBundleUtils.getReverseMapValue(currentPropertiesFile.getVirtualFile().getPath(), o.getValue()) : null;

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
                            fixes.add(getCreatePropertyFix(currentLang, resourceBundleName, allPropertiesFiles, LSFResourceBundleUtils.getDefaultBundleKey(o.getValue()), o));
                        } else if (existingPropertiesFiles.size() < propertiesFiles.size()) {
                            fixes.clear();
                            fixes.add(getCreatePropertyFix(currentLang, resourceBundleName, allPropertiesFiles.stream().filter(f -> !existingPropertiesFiles.contains(f)).collect(Collectors.toList()), currentKey, o));
                            break;
                        } else {
                            fixes.clear();
                            break;
                        }
                    }

                    if (!fixes.isEmpty()) {

                        Annotation annotation = myHolder.createWarningAnnotation(o.getTextRange(), "Missing " + o.getText() + " in resource bundle");
                        annotation.setEnforcedTextAttributes(WAVE_UNDERSCORED_WARNING);

                        for (IntentionAction fix : fixes) {
                            annotation.registerFix(fix);
                        }
                    }
                }
            }
        }
    }

    private IntentionAction getCreatePropertyFix(String currentLang, String resourceBundleName, List<PropertiesFile> propertiesFiles, String defaultKey, LSFLocalizedStringValueLiteral o) {
        return new IntentionAction() {
            @Override
            public void invoke(@NotNull Project project, Editor editor, PsiFile file) throws IncorrectOperationException {
                String defaultValue = o.getValue();
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

                    visitLocalizedStringValueLiteral(o);
                }
            }

            @Override
            public @IntentionName
            @NotNull
            String getText() {
                return "Create property for " + o.getText() + " in " + resourceBundleName;
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
                    addUnderscoredError(element, TextRange.create(element.getTextRange().getStartOffset() + i, element.getTextRange().getStartOffset() + i + 2), "Wrong escape sequence " + text.substring(i, i + 2));
                } else {
                    ++i;
                }
            }
        }
    }

    private boolean checkReference(LSFReference reference) {
        LSFResolvingError errorAnnotation = reference.resolveErrorAnnotation(myHolder);
        if (errorAnnotation != null) { // !isInMetaDecl(reference)
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
                myHolder.createWarningAnnotation(relationalPE, String.format("Type mismatch: can't cast %s to %s", class1, class2));
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

    private void addColumnInfo(PsiElement element, String tableName, String columnName) {
        final Annotation annotation = myHolder.createInfoAnnotation(element.getTextRange(), "Table: " + tableName + "; columnName: " + columnName);
        annotation.setEnforcedTextAttributes(IMPLICIT_DECL);
    }

    private void addAlreadyDefinedError(LSFDeclaration decl) {
        addAlreadyDefinedError(decl.getDuplicateElement(), decl.getPresentableText());
    }

    private void addAlreadyDefinedError(PsiElement element, String elementPresentableText) {
        addUnderscoredErrorWithResolving(element, "'" + elementPresentableText + "' is already defined");
    }

    private void addHighlightErrorWithResolving(PsiElement element, String message) {
        addErrorWithResolving(element, new LSFResolvingError(element, message, false));
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
        boolean inMetaDecl = isInMetaDecl(element);
        Annotation annotation;
        TextAttributes errorAttributes;
        if(inMetaDecl && hasResolving) {
            if (error.range != null)
                annotation = myHolder.createInfoAnnotation(error.range, error.text);
            else
                annotation = myHolder.createInfoAnnotation(error.element, error.text);
            errorAttributes = error.underscored ? WAVE_UNDERSCORED_META_ERROR : META_ERROR;
        } else {
            if (errorsSearchMode) {
                ShowErrorsAction.showErrorMessage(element, error.text, LSFErrorLevel.ERROR);
            }

            if (error.range != null)
                annotation = myHolder.createErrorAnnotation(error.range, error.text);
            else
                annotation = myHolder.createErrorAnnotation(error.element, error.text);
            errorAttributes = error.underscored ? WAVE_UNDERSCORED_ERROR : ERROR;
        }

        errorAttributes = mergeMetaAttributes(element, errorAttributes);
        annotation.setEnforcedTextAttributes(errorAttributes);
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

    private void addIndirectProp(LSFActionOrPropReference reference) {
        final Annotation annotation = myHolder.createWeakWarningAnnotation(reference.getTextRange(), "Indirect usage");
        TextAttributes error = IMPLICIT_DECL;
        error = mergeMetaAttributes(reference, error);
        annotation.setEnforcedTextAttributes(error);
    }

    private void addImplicitDecl(LSFReference reference) {
        final Annotation annotation = myHolder.createInfoAnnotation(reference.getTextRange(), "Implicit parameter declaration");
        TextAttributes error = IMPLICIT_DECL;
        error = mergeMetaAttributes(reference, error);
        annotation.setEnforcedTextAttributes(error);
    }

    private void addUntypedImplicitDecl(LSFReference reference) {
        final Annotation annotation = myHolder.createInfoAnnotation(reference.getTextRange(), "Untyped implicit parameter declaration");
        TextAttributes error = UNTYPED_IMPLICIT_DECL;
        error = mergeMetaAttributes(reference, error);
        annotation.setEnforcedTextAttributes(error);
    }

    @Override
    public void visitOverridePropertyStatement(@NotNull LSFOverridePropertyStatement element) {
        Result<LSFClassSet> required = new Result<>();
        Result<LSFClassSet> found = new Result<>();
        if (!LSFPsiImplUtil.checkOverrideValue(element, required, found)) {
            String requiredClass = required.getResult() instanceof LSFValueClass ? ((LSFValueClass) required.getResult()).getCaption() : required.getResult().getCanonicalName();
            String foundClass = found.getResult() instanceof LSFValueClass ? ((LSFValueClass) found.getResult()).getCaption() : found.getResult().getCanonicalName();
            addUnderscoredErrorWithResolving(element, "Wrong value class. Required : " + requiredClass + ", found : " + foundClass);
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
    public void visitSetObjectPropertyStatement(@NotNull LSFSetObjectPropertyStatement o) {
        String text = o.getText();
        if (text != null && text.contains("=")) {
            String property = text.substring(0, text.indexOf("=")).trim();
            if (!ASTCompletionContributor.validDesignProperty(property))
                addHighlightErrorWithResolving(o, "Can't resolve property " + property); // design property can be meta parameter
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
                leftClassList.stream().map(LSFClassSet::getCanonicalName).collect(Collectors.joining(", ")) + "; got: " +
                rightClassList.stream().map(LSFClassSet::getCanonicalName).collect(Collectors.joining(", "))); // uses resolving
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
                    myHolder.createWarningAnnotation(o, String.format("Type mismatch: can't cast %s to %s", leftClass, rightClass));
                }
            }
        }
    }

    @Override
    public void visitMapOptions(@NotNull LSFMapOptions o) {
        super.visitMapOptions(o);
        String option = o.getFirstChild().getText();
        if (!option.equals("'google'") && !option.equals("'yandex'")) {
            addUnderscoredError(o, "Wrong map option definition: 'google' or 'yandex' expected");
        }
    }
}
