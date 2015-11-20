package com.lsfusion.completion;

import com.intellij.codeInsight.TailType;
import com.intellij.codeInsight.completion.*;
import com.intellij.codeInsight.completion.impl.CamelHumpMatcher;
import com.intellij.codeInsight.lookup.LookupElement;
import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.CaretModel;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.progress.ProcessCanceledException;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Condition;
import com.intellij.openapi.util.TextRange;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiFileFactory;
import com.intellij.psi.ResolveState;
import com.intellij.psi.impl.source.PostprocessReformattingAspect;
import com.intellij.psi.impl.source.tree.TreeUtil;
import com.intellij.psi.scope.BaseScopeProcessor;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.stubs.StringStubIndexExtension;
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.BooleanValueHolder;
import com.intellij.util.ProcessingContext;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.indexes.*;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.psi.util.PsiTreeUtil.prevVisibleLeaf;
import static com.lsfusion.completion.ASTCompletionContributor.ClassUsagePolicy.*;
import static com.lsfusion.completion.CompletionUtils.createLookupElement;
import static com.lsfusion.completion.CompletionUtils.getVariantsFromIndices;
import static com.lsfusion.lang.LSFParserDefinition.NOT_KEYWORDS;
import static com.lsfusion.lang.parser.GeneratedParserUtilBase.*;
import static com.lsfusion.lang.psi.LSFPsiImplUtil.resolveParamClasses;
import static com.lsfusion.lang.psi.LSFTypes.*;
import static com.lsfusion.lang.psi.LSFTypes.Factory.getPsiElementClassByType;
import static com.lsfusion.lang.psi.references.impl.LSFFormElementReferenceImpl.FormExtendProcessor;
import static com.lsfusion.lang.psi.references.impl.LSFFormElementReferenceImpl.processFormContext;
import static com.lsfusion.util.LSFPsiUtils.getContextClasses;
import static java.util.Arrays.asList;

public class ASTCompletionContributor extends CompletionContributor {
    public static final Logger LOGGER = Logger.getInstance("com.lsfusion.completion.ASTCompletionContributor");

    private static final String[] LOGICAL_LITERALS = new String[]{"TRUE", "FALSE"};
    private static final String[] PRIMITIVE_TYPES = new String[]{"INTEGER", "LONG", "NUMERIC[,]", "DOUBLE",
                                                                 "DATE", "DATETIME", "TIME", "YEAR",
                                                                 "STRING[]", "ISTRING[]", "VARSTRING[]", "VARISTRING[]", "TEXT",
                                                                 "WORDFILE", "IMAGEFILE", "PDFFILE", "CUSTOMFILE", "EXCELFILE",
                                                                 "BOOLEAN", "COLOR"};

    enum ClassUsagePolicy {
        /**
         * must use all classes
         */
        MUST_USE_ALL,
        /**
         * must use only classes from context
         */
        MUST_USE_ANY,
        /**
         * may use classes outside the context
         */
        MAY_USE_ANY
    }

    public ASTCompletionContributor() {
        addCompletionProvider(CompletionType.BASIC);
        addCompletionProvider(CompletionType.SMART);
    }

    private void addCompletionProvider(final CompletionType completionType) {
        extend(completionType, psiElement().inFile(PlatformPatterns.instanceOf(LSFFile.class)), new CompletionProvider<CompletionParameters>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                if (CompletionUtils.isCompletionEnabled(parameters.getOriginalFile().getProject())) {
                    result = result.withPrefixMatcher(new CamelHumpMatcher(result.getPrefixMatcher().getPrefix(), false));
                    suggestByGuessingTokens(parameters, result);
                }
            }
        });
    }

    private static void suggestByGuessingTokens(@NotNull CompletionParameters parameters, @NotNull CompletionResultSet result) {
        if (parameters.getPosition().getContainingFile() instanceof LSFCodeFragment) {
            completeInFragment(parameters, result);
        } else {
            completeInFile(parameters, result);
        }
    }

    private static void completeInFile(CompletionParameters parameters, CompletionResultSet result) {
        PsiElement position = parameters.getPosition();
        PsiElement prev = prevVisibleLeaf(position);

        // общая логика
        PsiElement parent = PsiTreeUtil.getParentOfType(prev, LSFScriptStatement.class, LSFModuleHeader.class);
        while (prev != null && parent != null && !(prev.getTextLength() == 1 && prev.getText().equals(";"))) {
            prev = prevVisibleLeaf(parent);
            parent = PsiTreeUtil.getParentOfType(prev, LSFScriptStatement.class, LSFModuleHeader.class);
        }

        TextRange range = new TextRange(parent == null ? 0 : parent.getTextRange().getStartOffset(), position.getTextRange().getStartOffset());
        int originalOffsetShift = range.getStartOffset();

        PsiFile file = position.getContainingFile();
        String prefixText = range.substring(file.getText());
        if (parent != null && !(parent instanceof LSFModuleHeader) && prevVisibleLeaf(position) != null) {
            prefixText = "MODULE A;" + prefixText;
            originalOffsetShift -= "MODULE A;".length();
        }

        int completionOffset = StringUtil.isEmptyOrSpaces(prefixText) ? 0 : prefixText.length();
        String text = completionOffset == 0 ? "" : prefixText;

        MyCompletionCallback completionCallback = new MyCompletionCallback(text, originalOffsetShift, parameters, result, completionOffset);

        PsiFile tempFile = PsiFileFactory.getInstance(file.getProject()).createFileFromText("a.lsf", LSFLanguage.INSTANCE, text, true, false);
        tempFile.putUserData(COMPLETION_CALLBACK_KEY, completionCallback);
        TreeUtil.ensureParsed(tempFile.getNode());
    }

    private static void completeInFragment(CompletionParameters parameters, CompletionResultSet result) {
        PsiElement position = parameters.getPosition();

        assert position.getContainingFile() instanceof LSFCodeFragment;

        LSFCodeFragment file = (LSFCodeFragment) position.getContainingFile();

        String text = file.getText().substring(0, position.getTextRange().getStartOffset());

        MyCompletionCallback completionCallback = new MyCompletionCallback(text, 0, parameters, result, text.length());

        LSFCodeFragment fragment = new LSFCodeFragment(file.isExpression(), position.getProject(), file.getContext(), text);
        fragment.putUserData(COMPLETION_CALLBACK_KEY, completionCallback);
        TreeUtil.ensureParsed(fragment.getNode());
    }

    public static final InsertHandler keywordInsertHandler = new InsertHandler() {
        @Override
        public void handleInsert(InsertionContext context, LookupElement item) {
            Editor editor = context.getEditor();
            int tailOffset = context.getTailOffset();
            String lookupString = item.getLookupString();

            int brIndex = lookupString.lastIndexOf("[");
            if (brIndex != -1) {
                int tailLength = lookupString.length() - brIndex - 1;
                final CaretModel model = editor.getCaretModel();
                if (model.getOffset() == tailOffset) {
                    model.moveToOffset(tailOffset - tailLength);
                }
            } else {
                // c/p from com.intellij.codeInsight.lookup.TailTypeDecorator
                PostprocessReformattingAspect.getInstance(context.getProject()).doPostponedFormatting();
                TailType.insertChar(editor, tailOffset, ' ', true);
            }
        }
    };

    private static class MyCompletionCallback extends CompletionCallback {
        private final String text;
        private final int originalOffsetShift;
        private final CompletionResultSet result;
        private final LSFFile file;
        private final Project project;
        private final boolean isBasicCompletion;

        private GlobalSearchScope requireScope;

        private int framesCount;
        private List<Frame> frames;

        BooleanValueHolder namespaceCompleted = new BooleanValueHolder(false);
        BooleanValueHolder moduleCompleted = new BooleanValueHolder(false);
        BooleanValueHolder windowCompleted = new BooleanValueHolder(false);
        BooleanValueHolder tableCompleted = new BooleanValueHolder(false);
        BooleanValueHolder metaCompleted = new BooleanValueHolder(false);
        BooleanValueHolder classCompleted = new BooleanValueHolder(false);
        BooleanValueHolder groupCompleted = new BooleanValueHolder(false);
        BooleanValueHolder formCompleted = new BooleanValueHolder(false);
        BooleanValueHolder navigatorCompleted = new BooleanValueHolder(false);
        boolean staticObjectCompleted = false;
        BooleanValueHolder objectCompleted = new BooleanValueHolder(false);
        BooleanValueHolder groupObjectCompleted = new BooleanValueHolder(false);
        BooleanValueHolder propertyDrawCompleted = new BooleanValueHolder(false);
        BooleanValueHolder filterGroupCompleted = new BooleanValueHolder(false);
        boolean parameterCompleted = false;
        boolean propertyCompleted = false;

        long startTime = 0;

        public MyCompletionCallback(String text, int originalOffsetShift, CompletionParameters parameters, CompletionResultSet result, int completionOffset) {
            super(completionOffset);
            this.text = text;
            this.originalOffsetShift = originalOffsetShift;
            this.result = result;
            file = (LSFFile) parameters.getOriginalFile();
            project = file.getProject();
            isBasicCompletion = parameters.getCompletionType() == CompletionType.BASIC;

            startTime = System.nanoTime();
        }

        public GlobalSearchScope getRequireScope() {
            if (requireScope == null) {
                requireScope = file.getRequireScope();
            }
            return requireScope;
        }

        public void quickLog(String message) {
//            System.out.println("--------------------------------------------------- : " + (System.nanoTime() - startTime) / 1000000000.0);
//            System.out.println("   : " + message);
        }

        @Override
        public void addCompletionVariants(List<Frame> frames, IElementType elementType) {

            if (elementType == LEX_LOGICAL_LITERAL) {
                addKeywordVariants(LOGICAL_LITERALS);
            } else if (elementType == PRIMITIVE_TYPE) {
                addKeywordVariants(PRIMITIVE_TYPES);
            } else if (elementType == ID) {
                this.framesCount = frames.size();
                this.frames = frames;
                fillIdVariants();
            } else if (elementType instanceof LSFTokenType) {
                LSFTokenType lsfToken = (LSFTokenType) elementType;
                if (isKeyword(lsfToken)) {
                    addKeywordVariants(lsfToken.tokenText());
                }
            }
        }

        private boolean isKeyword(LSFTokenType lsfToken) {
            return !NOT_KEYWORDS.contains(lsfToken);
        }

        public void addKeywordVariants(String... keywords) {
            for (String keyword : keywords) {
                addLookupElement(
                        createLookupElement(keyword, 0, true, keywordInsertHandler)
                );
            }
        }

        private void addLookupElement(LookupElement lookupElement) {
            result.addElement(lookupElement);
        }

        private void addLookupElements(List<LookupElement> lookupElements) {
            result.addAllElements(lookupElements);
        }

        private void fillIdVariants() {
            boolean res = false;
            try {
                if (!res) res = completeNamespaceName();
                if (!res) res = completeModuleName();
                if (!res) res = completeWindowName();
                if (!res) res = completeTableName();
                if (!res) res = completeMetaName();
                if (!res) res = completeClassName();
                if (!res) res = completeGroupName();
                if (!res) res = completeFormName();
                if (!res) res = completeNavigatorName();
                if (!res) res = completeStaticObjectUsage();
                if (!res) res = completeObjectUsage();
                if (!res) res = completeGroupObjectUsage();
                if (!res) res = completeFilterGroupUsage();
                if (!res) res = completePropertyDrawUsage();
                if (!res) res = completeParameterUsage();
                if (!res) res = completePropertyUsage();
            } catch (ProcessCanceledException pce) {
                LOGGER.debug("ProcessCanceledException while filling ID completion variants ", pce);
            }
        }

        private boolean completeNamespaceName() {
            return completeFullNameUsage(namespaceCompleted, NAMESPACE_USAGE, asList(ModuleIndex.getInstance(), ExplicitNamespaceIndex.getInstance()), 1, false, true);
        }

        private boolean completeModuleName() {
            return completeFullNameUsage(moduleCompleted, MODULE_USAGE, asList(ModuleIndex.getInstance()), 1, false, false);
        }

        private boolean completeWindowName() {
            Collection<? extends LSFFullNameDeclaration> builtInWindows = LSFElementGenerator.getBuiltInWindows(project);
            return completeFullNameUsage(windowCompleted, WINDOW_USAGE, asList(WindowIndex.getInstance()), 1.5, true, true, builtInWindows);
        }

        private boolean completeTableName() {
            return completeFullNameUsage(tableCompleted, TABLE_USAGE, WindowIndex.getInstance());
        }

        private boolean completeMetaName() {
            return completeFullNameUsage(metaCompleted, METACODE_USAGE, MetaIndex.getInstance());
        }

        private boolean completeClassName() {
            return completeFullNameUsage(classCompleted, CUSTOM_CLASS_USAGE, ClassIndex.getInstance());
        }

        private boolean completeGroupName() {
            return completeFullNameUsage(groupCompleted, GROUP_USAGE, GroupIndex.getInstance());
        }

        private boolean completeFormName() {
            return completeFullNameUsage(formCompleted, FORM_USAGE, FormIndex.getInstance());
        }

        private boolean completeNavigatorName() {
            return completeFullNameUsage(navigatorCompleted, NAVIGATOR_ELEMENT_USAGE, asList(FormIndex.getInstance(), NavigatorElementIndex.getInstance()));
        }

        private boolean completeFullNameUsage(BooleanValueHolder completed, IElementType frameType, StringStubIndexExtension index) {
            return completeFullNameUsage(completed, frameType, asList(index));
        }

        private boolean completeFullNameUsage(BooleanValueHolder completed, IElementType frameType, Collection<? extends StringStubIndexExtension> indices) {
            return completeFullNameUsage(completed, frameType, indices, 1.5, true, true);
        }

        private boolean completeFullNameUsage(BooleanValueHolder completed, IElementType frameType, Collection<? extends StringStubIndexExtension> indices,
                                              double priority, boolean extractNamespace, boolean useRequiredScope) {
            return completeFullNameUsage(completed, frameType, indices, priority, extractNamespace, useRequiredScope, null);
        }
        
        private boolean completeFullNameUsage(BooleanValueHolder completed, IElementType frameType, Collection<? extends StringStubIndexExtension> indices,
                                              double priority, boolean extractNamespace, boolean useRequiredScope, Collection<? extends LSFFullNameDeclaration> additionalDeclarations) {
            Frame fullNameUsage = getLastFrameOfType(null, frameType);
            if (fullNameUsage != null) {
                if (!completed.getValue()) {
                    completed.setValue(true);
                    String namespaceName = extractNamespace ? extractNamespace() : null;
                    addLookupElements(
                            getVariantsFromIndices(namespaceName, file, indices, priority, useRequiredScope ? getRequireScope() : GlobalSearchScope.allScope(project))
                    );
                    
                    if (additionalDeclarations != null) {
                        for (LSFFullNameDeclaration decl : additionalDeclarations) {
                            if (namespaceName == null || namespaceName.equals(decl.getNamespaceName())) {
                                addLookupElement(createLookupElement(decl, 1.5));
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        }

        private boolean completeStaticObjectUsage() {
            Frame staticObjectId = getLastFrameOfType(null, STATIC_OBJECT_ID);
            if (staticObjectId != null) {
                if (!staticObjectCompleted) {
                    staticObjectCompleted = true;
                    String namespaceAndClassName[] = extractClassNameAndNamespaceForStaticObjectId();
                    String namespace = namespaceAndClassName[0];
                    String className = namespaceAndClassName[1];

                    Collection<LSFClassDeclaration> classDeclarations = LSFGlobalResolver.findElements(className, namespace, file, Collections.singleton(LSFStubElementTypes.CLASS), Condition.TRUE);
                    for (LSFClassDeclaration classDecl : classDeclarations) {
                        for (LSFClassExtend classExtend : LSFGlobalResolver.findExtendElements(classDecl, LSFStubElementTypes.EXTENDCLASS, project, getRequireScope())) {
                            for (LSFStaticObjectDeclaration staticDecl : classExtend.getStaticObjects()) {
                                addLookupElement(createLookupElement(staticDecl));
                            }
                        }
                    }
                }
                return true;
            }

            return false;
        }

        private boolean completeObjectUsage() {
            return completeFormContextObject(
                    objectCompleted,
                    OBJECT_USAGE,
                    new FormExtendProcessor<LSFObjectDeclaration>() {
                        @Override
                        public Collection<LSFObjectDeclaration> process(LSFFormExtend formExtend) {
                            return formExtend.getObjectDecls();
                        }
                    }
            );
        }

        private boolean completeGroupObjectUsage() {
            return completeFormContextObject(
                    groupObjectCompleted,
                    GROUP_OBJECT_USAGE,
                    new FormExtendProcessor<LSFGroupObjectDeclaration>() {
                        @Override
                        public Collection<LSFGroupObjectDeclaration> process(LSFFormExtend formExtend) {
                            return formExtend.getGroupObjectDecls();
                        }
                    }
            );
        }

        private boolean completeFilterGroupUsage() {
            return completeFormContextObject(
                    filterGroupCompleted,
                    FILTER_GROUP_USAGE,
                    new FormExtendProcessor<LSFFilterGroupDeclaration>() {
                        @Override
                        public Collection<LSFFilterGroupDeclaration> process(LSFFormExtend formExtend) {
                            return formExtend.getFilterGroupDecls();
                        }
                    }
            );
        }

        private boolean completePropertyDrawUsage() {
            return completeFormContextObject(
                    propertyDrawCompleted,
                    FORM_PROPERTY_DRAW_USAGE,
                    new FormExtendProcessor<LSFPropertyDrawDeclaration>() {
                        @Override
                        public Collection<LSFPropertyDrawDeclaration> process(LSFFormExtend formExtend) {
                            return formExtend.getPropertyDrawDecls();
                        }
                    }
            );
        }

        private <T extends LSFDeclaration> boolean completeFormContextObject(BooleanValueHolder completed, IElementType frameType, FormExtendProcessor<T> formExtendProcessor) {
            Frame elementUsage = getLastFrameOfType(null, frameType);
            if (elementUsage != null) {
                if (!completed.getValue()) {
                    completed.setValue(true);
                    FormContext psi = getLastPsiOfType(FormContext.class);
                    if (psi != null) {
                        Set<T> declaration = processFormContext(psi, elementUsage.offset, formExtendProcessor);
                        for (T elementDecl : declaration) {
                            addLookupElement(createLookupElement(elementDecl));
                        }
                    }
                }
                return true;
            }
            return false;
        }

        private boolean completeParameterUsage() {
            Frame paramDeclare = getLastFrameOfType(null, PARAM_DECLARE);
            if (paramDeclare != null) {
                if (!parameterCompleted) {
                    parameterCompleted = true;
                    PsiElement psi = getLastPsiOfType(ModifyParamContext.class);
                    if (psi != null) {
                        LSFExprParamDeclaration currentParamDeclaration = getPsiOfTypeForFrame(paramDeclare, LSFExprParamDeclaration.class);
                        for (LSFExprParamDeclaration paramDeclaration : LSFPsiUtils.getContextParams(psi, getOriginalFrameOffset(paramDeclare), false)) {
                            if (paramDeclaration != currentParamDeclaration) {
                                addLookupElement(createLookupElement(paramDeclaration));
                            }
                        }
                    }
                }
                return true;
            }
            return false;
        }

        private boolean completePropertyUsage() {
            Frame propUsage = getLastFrameOfType(null, PROPERTY_USAGE);
            if (propUsage != null) {
                if (!propertyCompleted) {
                    quickLog("Completing propertyUsage");
                    propertyCompleted = true;

                    boolean res = completePropertyInContextOfFormPropertyObject(propUsage);
                    if (!res) res = completePropertyInContextOfMappedPropertiesList(propUsage);
                    if (!res) res = completePropertyInFormContext(propUsage);
                    if (!res) res = completePropertyInNoContext(propUsage);
                    if (!res) res = completePropertyInModifyParamContext(propUsage);
                    quickLog("Completed propertyUsage");
                }

                return true;
            }
            return false;
        }

        private boolean completePropertyInNoContext(Frame propUsage) {
            Frame frame = getLastFrameOfType(propUsage, NO_CONTEXT_PROPERTY_USAGE);
            if (frame != null) {
                completeProperties(Collections.EMPTY_LIST, MAY_USE_ANY, true);
                return true;
            }
            return false;
        }

        private boolean completePropertyInModifyParamContext(Frame propUsage) {
            ModifyParamContext psi = getLastPsiOfType(ModifyParamContext.class);
            if (psi != null) {
                completeProperties(getContextClasses(psi, getOriginalFrameOffset(propUsage), false), MAY_USE_ANY);
                return true;
            }
            return false;
        }

        private boolean completePropertyInContextOfFormPropertyObject(Frame propUsage) {
            Frame formMappedProperty = getLastFrameOfType(propUsage, FORM_CALC_PROPERTY_OBJECT, FORM_ACTION_PROPERTY_OBJECT);
            return formMappedProperty != null && completePropertyInFormContext(formMappedProperty);
        }

        private boolean completePropertyInContextOfMappedPropertiesList(Frame propUsage) {
            Frame mappedPropertiesList = getLastFrameOfType(propUsage, FORM_MAPPED_NAME_PROPERTIES_LIST);
            if (mappedPropertiesList != null) {
                LSFFormMappedNamePropertiesList psiMappedPropertiesList = getPsiOfTypeForFrame(mappedPropertiesList, LSFFormMappedNamePropertiesList.class);
                if (psiMappedPropertiesList != null) {
                    completeProperties(
                            resolveParamClasses(psiMappedPropertiesList),
                            MUST_USE_ALL
                    );
                }
                return true;
            }
            return false;
        }

        private boolean completePropertyInFormContext(Frame propUsage) {
            Frame formStatement = getLastFrameOfType(propUsage, FORM_STATEMENT);
            if (formStatement != null) {
                completePropertyInContextOfFormStatement(formStatement);
                return true;
            }
            return false;
        }

        private void completePropertyInContextOfFormStatement(Frame startFrom) {
            LSFFormStatement psiFormStatement = getFormStatementPSI(startFrom);
            if (psiFormStatement != null) {
                completeProperties(getContextClasses(psiFormStatement, true), MUST_USE_ANY);
            }
        }

        private LSFFormStatement getFormStatementPSI(Frame childFrame) {
            Frame formStatement = getLastFrameOfType(childFrame, FORM_STATEMENT);
            if (formStatement != null) {
                return getPsiOfTypeForFrame(formStatement, LSFFormStatement.class);
            }
            return null;
        }

        public <G extends LSFFullNameDeclaration> List<G> getDeclarationsFromScope(Project project, GlobalSearchScope scope, StringStubIndexExtension index) {
            List<G> result = new ArrayList<G>();

            Collection<String> propKeys = index.getAllKeys(project);
            quickLog("After getAllKeys: " + propKeys.size());
            for (String propKey : propKeys) {
                Collection<G> declarations = index.get(propKey, project, scope);
                for (G declaration : declarations) {
                    result.add(declaration);
                }
            }

            return result;
        }

        private void completeProperties(List<LSFClassSet> contextClasses, ClassUsagePolicy classUsagePolicy) {
            completeProperties(contextClasses, classUsagePolicy, false);
        }
        
        private void completeProperties(List<LSFClassSet> contextClasses, ClassUsagePolicy classUsagePolicy, boolean forceAll) {
            quickLog("After getContextClasses..");

            String namespaceName = extractNamespace();

            for (LSFClassSet classSet : contextClasses) {
                if (classSet != null) {
                    LSFValueClass valueClass = classSet.getCommonClass();
                    if (valueClass != null) {
                        addDeclarationsToLookup(contextClasses, classUsagePolicy, namespaceName, LSFPsiUtils.getPropertiesApplicableToClass(valueClass, project, requireScope));
                    }
                }
            }

            quickLog("After getDeclarationsFromScope..");
            
            final Collection<LSFLocalDataPropertyDefinition> localDeclarations = new ArrayList<LSFLocalDataPropertyDefinition>();
            // search local properties
            PsiElement lastElement = getLastPsiOfType(PsiElement.class);
            if (lastElement != null) {
                PsiTreeUtil.treeWalkUp(new BaseScopeProcessor() {
                    @Override
                    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
                        if (element instanceof LSFLocalDataPropertyDefinition) {
                            localDeclarations.add((LSFLocalDataPropertyDefinition) element);
                        }
                        return true;
                    }
                }, lastElement, null, new ResolveState());
            }

            addDeclarationsToLookup(contextClasses, classUsagePolicy, null, localDeclarations);

            quickLog("After LOCAL searching..");

            if (!isBasicCompletion || forceAll) {
                //search any other declarations
                Collection<LSFPropertyStatement> globalDeclarations = getDeclarationsFromScope(project, getRequireScope(), PropIndex.getInstance());
                addDeclarationsToLookup(contextClasses, classUsagePolicy, namespaceName, globalDeclarations);
            }
        }

        private <G extends LSFPropDeclaration> void addDeclarationsToLookup(List<LSFClassSet> contextClasses, ClassUsagePolicy classUsagePolicy, String namespace, Collection<G> declarations) {
            boolean useAll = classUsagePolicy == MUST_USE_ALL;

            for (G declaration : declarations) {
                assert namespace == null || declaration instanceof LSFGlobalPropDeclaration;

                if (namespace != null && !namespace.equals(((LSFGlobalPropDeclaration)declaration).getNamespaceName())) {
                    continue;
                }

                int priority = 2;

                List<LSFClassSet> declClasses = declaration.resolveParamClasses();
                if (declClasses != null) {
                    if (useAll) {
                        if (declClasses.size() != contextClasses.size()) {
                            priority = -1;
                        } else {
                            for (int i = 0; i < declClasses.size(); ++i) {
                                LSFClassSet declClass = declClasses.get(i);
                                LSFClassSet contextClass = contextClasses.get(i);

                                if (declClass != null && contextClass != null && !declClass.containsAll(contextClass, true)) {
                                    priority = -1;
                                    break;
                                }
                            }
                            if(priority != -1)
                                priority += declClasses.size();
                        }
                    } else {
                        for (LSFClassSet declClass : declClasses) {
                            if (declClass != null) {
                                boolean foundInContext = false;
                                for(LSFClassSet contextClass : contextClasses) {
                                    if(declClass.containsAll(contextClass, true)) {
                                        foundInContext = true;
                                        break;
                                    }
                                }
                                
                                if (foundInContext) {
                                    priority++;
                                } else {
                                    if (classUsagePolicy == MUST_USE_ANY) {
                                        priority = -1;
                                        break;
                                    }
                                }
                            }
                        }
                    }
                }
                if (priority > 0) {
                    addLookupElement(
                            createLookupElement(
                                    declaration.getName(), declaration,
                                    declaration.getSignaturePresentableText(),
                                    declaration.getLSFFile().getName(),
                                    declaration.getIcon(0),
                                    priority
                            ));
                }
            }
        }

        private String extractNamespace() {
            // с конца идут фреймы simpleName, compoundId
            Frame simpleName = frames.get(framesCount - 1);
            Frame compoundId = frames.get(framesCount - 2);
            if (compoundId.offset == simpleName.offset) {
                return null;
            }

            String namespaceText = text.substring(compoundId.offset, simpleName.offset);
            int dotIndex = namespaceText.lastIndexOf('.');
            return namespaceText.substring(0, dotIndex).trim();
        }

        private String[] extractClassNameAndNamespaceForStaticObjectId() {
            // с конца идут фреймы simpleName, staticObjectId
            Frame simpleName = frames.get(framesCount - 1);
            Frame staticObjectId = frames.get(framesCount - 2);
            assert staticObjectId.offset != simpleName.offset;

            String namespaceAndClassNameText = text.substring(staticObjectId.offset, simpleName.offset);
            int dotIndex = namespaceAndClassNameText.lastIndexOf('.');
            int dot2Index = namespaceAndClassNameText.lastIndexOf('.', dotIndex - 1);
            if (dot2Index == -1) {
                // no namespace
                return new String[]{
                        null,
                        namespaceAndClassNameText.substring(0, dotIndex).trim()
                };
            } else {
                //with namespace
                return new String[]{
                        namespaceAndClassNameText.substring(0, dot2Index).trim(),
                        namespaceAndClassNameText.substring(dot2Index + 1, dotIndex).trim()
                };
            }
        }

        private Frame getLastFrameOfType(Frame startFromFrame, IElementType... frameTypes) {
            int startIndex = startFromFrame == null ? -1 : frames.lastIndexOf(startFromFrame);
            if (startIndex == -1) {
                startIndex = framesCount - 1;
            }
            for (int i = startIndex; i >= 0; --i) {
                Frame frame = frames.get(i);
                for (IElementType frameType : frameTypes) {
                    if (frame.type == frameType) {
                        return frame;
                    }
                }
            }
            return null;
        }

        @Nullable
        private <T extends PsiElement> T getLastPsiOfType(@NotNull Class<T> psiClass) {
            try {
                T result = null;
                for (int i = 0; i < framesCount; ++i) {
                    Frame currFrame = frames.get(i);
                    IElementType elementType = currFrame.type;
                    if (i == 0 || (elementType != null && !(elementType instanceof LSFTokenType))) {
                        PsiElement psiElement = i == 0 ? file : getPsiOfTypeForFrame(currFrame, getPsiElementClassByType(elementType));
                        if (psiElement == null) {
                            break;
                        }
                        if (psiClass.isInstance(psiElement)) {
                            result = (T) psiElement;
                        }
                    }
                }

                return result;
            } catch (ProcessCanceledException pce) {
                return null;
            }
        }

        @Nullable
        private <T extends PsiElement> T getPsiOfTypeForFrame(Frame frame, Class<T> psiClass) {
            try {
                return PsiTreeUtil.findElementOfClassAtOffset(file, getOriginalFrameOffset(frame), psiClass, false);
            } catch (ProcessCanceledException pce) {
                return null;
            }
        }
        
        private int getOriginalFrameOffset(Frame frame) {
            return originalOffsetShift + frame.offset;
        }
    }
}
