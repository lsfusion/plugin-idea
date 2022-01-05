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
import com.intellij.openapi.util.Conditions;
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
import com.intellij.psi.tree.IElementType;
import com.intellij.psi.util.PsiTreeUtil;
import com.intellij.util.BooleanValueHolder;
import com.intellij.util.ProcessingContext;
import com.lsfusion.design.model.proxy.ViewProxy;
import com.lsfusion.design.model.proxy.ViewProxyFactory;
import com.lsfusion.lang.LSFElementGenerator;
import com.lsfusion.lang.LSFLanguage;
import com.lsfusion.lang.classes.LSFClassSet;
import com.lsfusion.lang.classes.LSFValueClass;
import com.lsfusion.lang.psi.*;
import com.lsfusion.lang.psi.context.FormContext;
import com.lsfusion.lang.psi.context.ModifyParamContext;
import com.lsfusion.lang.psi.declarations.*;
import com.lsfusion.lang.psi.declarations.impl.LSFFormExtendElement;
import com.lsfusion.lang.psi.extend.LSFClassExtend;
import com.lsfusion.lang.psi.extend.LSFDesign;
import com.lsfusion.lang.psi.extend.LSFFormExtend;
import com.lsfusion.lang.psi.extend.impl.LSFClassExtendImpl;
import com.lsfusion.lang.psi.extend.impl.LSFDesignImpl;
import com.lsfusion.lang.psi.extend.impl.LSFFormExtendImpl;
import com.lsfusion.lang.psi.indexes.*;
import com.lsfusion.lang.psi.stubs.types.LSFStubElementTypes;
import com.lsfusion.util.LSFPsiUtils;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;

import static com.intellij.patterns.PlatformPatterns.psiElement;
import static com.intellij.psi.util.PsiTreeUtil.prevVisibleLeaf;
import static com.lsfusion.completion.ASTCompletionContributor.ClassUsagePolicy.*;
import static com.lsfusion.completion.CompletionUtils.createLookupElement;
import static com.lsfusion.completion.CompletionUtils.getVariantsFromIndices;
import static com.lsfusion.lang.LSFParserDefinition.NOT_KEYWORDS;
import static com.lsfusion.lang.parser.GeneratedParserUtilBase.*;
import static com.lsfusion.lang.psi.LSFTypes.*;
import static com.lsfusion.lang.psi.LSFTypes.Factory.getPsiElementClassByType;
import static com.lsfusion.util.LSFPsiUtils.getContextClasses;
import static java.util.Arrays.asList;

public class ASTCompletionContributor extends CompletionContributor {
    public static final Logger LOGGER = Logger.getInstance("com.lsfusion.completion.ASTCompletionContributor");

    private static final String[] LOGICAL_LITERALS = new String[]{"TRUE", "FALSE"};
    private static final String[] T_LOGICAL_LITERALS = new String[]{"TTRUE", "TFALSE"};
    private static final String[] PRIMITIVE_TYPES = new String[]{"INTEGER", "LONG", "NUMERIC", "NUMERIC[,]", "DOUBLE",
                                                                 "DATE", "DATETIME", "TIME", "YEAR", "ZDATETIME",
                                                                 "INTERVAL[DATE]", "INTERVAL[TIME]", "INTERVAL[DATETIME]", "INTERVAL[ZDATETIME]",
                                                                 "BPSTRING[]", "BPISTRING[]", "STRING[]", "ISTRING[]",
                                                                 "BPSTRING", "BPISTRING", "STRING", "ISTRING", "TEXT", "RICHTEXT", "HTMLTEXT",
                                                                 "WORDFILE", "IMAGEFILE", "PDFFILE", "DBFFILE", "RAWFILE", "FILE", "EXCELFILE",
                                                                 "TEXTFILE", "CSVFILE", "HTMLFILE", "JSONFILE", "XMLFILE", "TABLEFILE", "NAMEDFILE",
                                                                 "WORDLINK", "IMAGELINK", "PDFLINK", "DBFLINK", "RAWLINK", "LINK", "EXCELLINK",
                                                                 "TEXTLINK", "CSVLINK", "HTMLLINK", "JSONLINK", "XMLLINK", "TABLELINK",
                                                                 "BOOLEAN", "TBOOLEAN", "COLOR"};

    private static final Set<String> DESIGN_PROPERTIES = new LinkedHashSet<>() {
        {
            for (Class<? extends ViewProxy> aClass : ViewProxyFactory.PROXY_CLASSES.values()) {
                for (Method method : aClass.getDeclaredMethods()) {
                    if (method.getName().startsWith("set")) {
                        add(Character.toLowerCase(method.getName().charAt(3)) + method.getName().substring(4));
                    }
                }
            }
        }
    };

    public static boolean validDesignProperty(String designProperty) {
        return DESIGN_PROPERTIES.contains(designProperty);
    }

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
        extend(completionType, psiElement().inFile(PlatformPatterns.instanceOf(LSFFile.class)), new CompletionProvider<>() {
            @Override
            protected void addCompletions(@NotNull CompletionParameters parameters, ProcessingContext context, @NotNull CompletionResultSet result) {
                if (CompletionUtils.isCompletionEnabled(parameters.getOriginalFile().getProject()) && 
                        parameters.getPosition().getContainingFile() instanceof LSFFile &&
                        parameters.getOriginalFile() instanceof LSFFile) {
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

        completeScript(new ScriptCreator() {
            public PsiFile create(String text, Project project) {
                return PsiFileFactory.getInstance(project).createFileFromText("a.lsf", LSFLanguage.INSTANCE, text, true, false);
            }
        }, parameters, result, originalOffsetShift, file.getProject(), completionOffset, text);
    }

    private static void completeInFragment(CompletionParameters parameters, CompletionResultSet result) {
        PsiElement position = parameters.getPosition();

        assert position.getContainingFile() instanceof LSFCodeFragment;

        final LSFCodeFragment file = (LSFCodeFragment) position.getContainingFile();

        String text = file.getText().substring(0, position.getTextRange().getStartOffset());

        completeScript(new ScriptCreator() {
            public PsiFile create(String text, Project project) {
                return new LSFCodeFragment(file.isExpression(), project, file.getContext(), text);
            }
        }, parameters, result, 0, position.getProject(), text.length(), text);
    }

    private interface ScriptCreator {
        PsiFile create(String text, Project project);
    }

    private enum ExecType {
        BASE, LIGHT, HEAVY, ALL;

        public boolean isProps() {
            return this != ExecType.BASE || this == ExecType.ALL;
        }

        public boolean isLight() {
            assert isProps();
            return this == ExecType.LIGHT || this == ExecType.ALL;
        }

        public boolean isHeavy() {
            assert isProps();
            return this == ExecType.HEAVY || this == ExecType.ALL;
        }

        public boolean isBase() {
            return this == ExecType.BASE || this == ExecType.ALL;
        }
    }

    private static void completeScript(ScriptCreator creator, CompletionParameters parameters, CompletionResultSet result, int originalOffsetShift, Project project, int completionOffset, String text) {
        completeScript(creator, parameters, result, originalOffsetShift, project, completionOffset, text, ExecType.BASE);
        completeScript(creator, parameters, result, originalOffsetShift, project, completionOffset, text, ExecType.LIGHT);
        completeScript(creator, parameters, result, originalOffsetShift, project, completionOffset, text, ExecType.HEAVY);
    }

    private static void completeScript(ScriptCreator creator, CompletionParameters parameters, CompletionResultSet result, int originalOffsetShift, Project project, int completionOffset, String text, ExecType type) {
        PsiFile tempFile = creator.create(text, project);
        MyCompletionCallback completionCallback = new MyCompletionCallback(text, originalOffsetShift, parameters, result, completionOffset, type);
        tempFile.putUserData(COMPLETION_CALLBACK_KEY, completionCallback);
        TreeUtil.ensureParsed(tempFile.getNode());
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
        private LSFLocalSearchScope localScope;

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
        BooleanValueHolder componentCompleted = new BooleanValueHolder(false);
        boolean parameterCompleted = false;
        boolean propertyCompleted = false;
        boolean actionCompleted = false;
        boolean propertyElseActionCompleted = false;
        boolean formElseNoParamsActionCompleted = false;
        final ExecType type;

        private static final double NAMESPACE_PRIORITY = 1.5;
        private static final double MODULE_PRIORITY = 1.5;
        private static final double WINDOW_PRIORITY = 1.5;
        private static final double NAVIGATOR_PRIORITY = 1.5;
        private static final double TABLE_PRIORITY = 1.5;
        private static final double METACODE_PRIORITY = 1.5;
        private static final double CUSTOM_CLASS_PRIORITY = 1.5;
        private static final double GROUP_PRIORITY = 1.5;
        private static final double FORM_PRIORITY = 1.5;

        private static final double STATIC_PRIORITY = 5;
        private static final double FORM_OBJECT_PRIORITY = 5;
        private static final double DESIGN_PRIORITY = 5;
        private static final double PARAM_PRIORITY = 5;

        private static final double PROPERTY_PRIORITY = 2; // при USE_ANY еще добавляются количество подходящих классов
        private static final double ACTION_PRIORITY = 2; // при USE_ANY еще добавляются количество подходящих классов

        private static final double KEYWORD_PRIORITY = 2;

        long startTime = 0;

        public MyCompletionCallback(String text, int originalOffsetShift, CompletionParameters parameters, CompletionResultSet result, int completionOffset, ExecType type) {
            super(completionOffset);
            this.text = text;
            this.originalOffsetShift = originalOffsetShift;
            this.result = result;
            file = (LSFFile) parameters.getOriginalFile();
            project = file.getProject();
            isBasicCompletion = parameters.getCompletionType() == CompletionType.BASIC;
            this.type = type;

            startTime = System.nanoTime();
        }

        public GlobalSearchScope getRequireScope() {
            if (requireScope == null) {
                requireScope = file.getRequireScope();
            }
            return requireScope;
        }

        public LSFLocalSearchScope getLocalScope() {
            if (localScope == null)
                localScope = new LSFLocalSearchScope(() -> file, () -> getLastPsiOfType(LSFMetaCodeDeclarationStatement.class));
            return localScope;
        }

        public void quickLog(String message) {
//            System.out.println("--------------------------------------------------- : " + (System.nanoTime() - startTime) / 1000000000.0);
//            System.out.println("   : " + message);
        }

        @Override
        public void addCompletionVariants(List<Frame> frames, IElementType elementType) {

            if (elementType == LEX_LOGICAL_LITERAL) {
                addKeywordVariants(LOGICAL_LITERALS);
            } else if (elementType == LEX_T_LOGICAL_LITERAL) {
                addKeywordVariants(T_LOGICAL_LITERALS);
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
            if(type.isBase()) {
                for (String keyword : keywords) {
                    addLookupElement(
                            createLookupElement(keyword, KEYWORD_PRIORITY, true, keywordInsertHandler)
                    );
                }
            }
        }

        private void addLookupElement(LookupElement lookupElement) {
            if (lookupElement != null) {
                result.addElement(lookupElement);
            }
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
                if (!res) res = completeComponentUsage();
                if (!res) res = completePropertyDrawUsage();
                if (!res) res = completeParameterUsage();
                if (!res) res = completePropertyUsage();
                if (!res) res = completeActionUsage();
                if (!res) res = completePropertyElseActionUsage();
                if (!res) res = completeFormElseNoParamsActionUsage();
                if (!res) res = completeDesignProperties();
            } catch (ProcessCanceledException pce) {
                LOGGER.debug("ProcessCanceledException while filling ID completion variants ", pce);
            }
        }

        private boolean completeNamespaceName() {
            return completeFullNameUsage(namespaceCompleted, NAMESPACE_USAGE, asList(ModuleIndex.getInstance(), ExplicitNamespaceIndex.getInstance()), NAMESPACE_PRIORITY, false, true);
        }

        private boolean completeModuleName() {
            return completeFullNameUsage(moduleCompleted, MODULE_USAGE, asList(ModuleIndex.getInstance()), MODULE_PRIORITY, false, false);
        }

        private boolean completeWindowName() {
            Collection<? extends LSFFullNameDeclaration> builtInWindows = LSFElementGenerator.getBuiltInWindows(project);
            return completeFullNameUsage(windowCompleted, WINDOW_USAGE, asList(WindowIndex.getInstance()), WINDOW_PRIORITY, true, true, builtInWindows);
        }

        private boolean completeTableName() {
            return completeFullNameUsage(tableCompleted, TABLE_USAGE, TableIndex.getInstance(), TABLE_PRIORITY);
        }

        private boolean completeMetaName() {
            return completeFullNameUsage(metaCompleted, METACODE_USAGE, MetaIndex.getInstance(), METACODE_PRIORITY);
        }

        private boolean completeClassName() {
            return completeFullNameUsage(classCompleted, CUSTOM_CLASS_USAGE, ClassIndex.getInstance(), CUSTOM_CLASS_PRIORITY);
        }

        private boolean completeGroupName() {
            return completeFullNameUsage(groupCompleted, GROUP_USAGE, GroupIndex.getInstance(), GROUP_PRIORITY);
        }

        private boolean completeFormName() {
            return completeFullNameUsage(formCompleted, FORM_USAGE, FormIndex.getInstance(), FORM_PRIORITY);
        }

        private boolean completeNavigatorName() {
            return completeFullNameUsage(navigatorCompleted, NAVIGATOR_ELEMENT_USAGE, asList(FormIndex.getInstance(), NavigatorElementIndex.getInstance()), NAVIGATOR_PRIORITY);
        }

        private boolean completeFullNameUsage(BooleanValueHolder completed, IElementType frameType, LSFStringStubIndex index, double priority) {
            return completeFullNameUsage(completed, frameType, asList(index), priority);
        }

        private boolean completeFullNameUsage(BooleanValueHolder completed, IElementType frameType, Collection<? extends LSFStringStubIndex> indices, double priority) {
            return completeFullNameUsage(completed, frameType, indices, priority, true, true);
        }

        private boolean completeFullNameUsage(BooleanValueHolder completed, IElementType frameType, Collection<? extends LSFStringStubIndex> indices,
                                              double priority, boolean extractNamespace, boolean useRequiredScope) {
            return completeFullNameUsage(completed, frameType, indices, priority, extractNamespace, useRequiredScope, null);
        }
        
        private boolean completeFullNameUsage(BooleanValueHolder completed, IElementType frameType, Collection<? extends LSFStringStubIndex> indices,
                                              double priority, boolean extractNamespace, boolean useRequiredScope, Collection<? extends LSFFullNameDeclaration> additionalDeclarations) {
            Frame fullNameUsage = getLastFrameOfType(null, frameType);
            if (fullNameUsage != null) {
                if (type.isBase() && !completed.getValue()) {
                    completed.setValue(true);
                    String namespaceName = extractNamespace ? extractNamespace() : null;
                    addLookupElements(
                            getVariantsFromIndices(namespaceName, file, indices, priority, useRequiredScope ? getRequireScope() : GlobalSearchScope.allScope(project), useRequiredScope ? getLocalScope() : LSFLocalSearchScope.GLOBAL)
                    );
                    
                    if (additionalDeclarations != null) {
                        for (LSFFullNameDeclaration decl : additionalDeclarations) {
                            if (namespaceName == null || namespaceName.equals(decl.getNamespaceName())) {
                                addLookupElement(createLookupElement(decl, priority));
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
                if (type.isBase() && !staticObjectCompleted) {
                    staticObjectCompleted = true;
                    String namespaceAndClassName[] = extractClassNameAndNamespaceForStaticObjectId();
                    String namespace = namespaceAndClassName[0];
                    String className = namespaceAndClassName[1];

                    LSFLocalSearchScope localScope = getLocalScope();
                    for (LSFClassDeclaration classDecl : LSFGlobalResolver.findElements(className, namespace, file, null, localScope, Collections.singleton(LSFStubElementTypes.CLASS), Conditions.alwaysTrue())) {
                        for (LSFStaticObjectDeclaration staticDecl : LSFClassExtendImpl.processClassContext(classDecl, file, getOriginalFrameOffset(staticObjectId), localScope, LSFClassExtend::getStaticObjects)) {
                            addLookupElement(createLookupElement(staticDecl, STATIC_PRIORITY));
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
                    LSFFormExtend::getObjectDecls
            );
        }

        private boolean completeGroupObjectUsage() {
            return completeFormContextObject(
                    groupObjectCompleted,
                    GROUP_OBJECT_USAGE,
                    LSFFormExtend::getGroupObjectDecls
            );
        }

        private boolean completeFilterGroupUsage() {
            return completeFormContextObject(
                    filterGroupCompleted,
                    FILTER_GROUP_USAGE,
                    LSFFormExtend::getFilterGroupDecls
            );
        }

        private boolean completeComponentUsage() {
            return completeDesignContextObject(
                    componentCompleted,
                    COMPONENT_USAGE,
                    LSFDesign::getComponentDecls
            );
        }

        private boolean completePropertyDrawUsage() {
            return completeFormContextObject(
                    propertyDrawCompleted,
                    FORM_PROPERTY_DRAW_USAGE,
                    LSFFormExtend::getPropertyDrawDecls
            );
        }

        private <T extends LSFFormExtendElement> boolean completeFormContextObject(BooleanValueHolder completed, IElementType frameType, Function<LSFFormExtend, Collection<T>> formExtendProcessor) {
            Frame elementUsage = getLastFrameOfType(null, frameType);
            if (elementUsage != null) {
                if (type.isBase() && !completed.getValue()) {
                    completed.setValue(true);
                    FormContext psi = getLastPsiOfType(FormContext.class);
                    if (psi != null) {
                        Set<T> declaration = LSFFormExtendImpl.processFormContext(psi, getOriginalFrameOffset(elementUsage), getLocalScope(), formExtendProcessor);
                        for (T elementDecl : declaration) {
                            addLookupElement(createLookupElement(elementDecl, FORM_OBJECT_PRIORITY));
                        }
                    }
                }
                return true;
            }
            return false;
        }

        private <T extends LSFDesignElementDeclaration<T>> boolean completeDesignContextObject(BooleanValueHolder completed, IElementType frameType, Function<LSFDesign, Collection<T>> designProcessor) {
            Frame elementUsage = getLastFrameOfType(null, frameType);
            if (elementUsage != null) {
                if (type.isBase() && !completed.getValue()) {
                    completed.setValue(true);
                    FormContext psi = getLastPsiOfType(FormContext.class);
                    if (psi != null) {
                        Set<T> declaration = LSFDesignImpl.processDesignContext(psi, getOriginalFrameOffset(elementUsage), getLocalScope(), designProcessor);
                        for (T elementDecl : declaration) {
                            addLookupElement(createLookupElement(elementDecl, DESIGN_PRIORITY));
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
                if (type.isBase() && !parameterCompleted) {
                    parameterCompleted = true;
                    quickLog("Completing parameter");

                    boolean res = false;
                    if (!res) res = completeParameterInModifyParamOrFormContext(paramDeclare);
                    quickLog("Completed parameter");
                }
                return true;
            }
            return false;
        }

        private boolean completePropertyUsage() {
            Frame propUsage = getLastFrameOfType(null, PROPERTY_USAGE);
            if (propUsage != null) {
                if (type.isProps() && !propertyCompleted) {
                    propertyCompleted = true;
                    quickLog("Completing propertyUsage");

                    boolean res = false;
                    if (!res) res = completePropertyInImportUsage(propUsage);
                    if (!res) res = completePropertyInNoContext(propUsage);
                    if (!res) res = completePropertyInModifyParamOrFormContext(propUsage);
                    quickLog("Completed propertyUsage");
                }
                return true;
            }
            return false;
        }

        private boolean completeActionUsage() {
            Frame propUsage = getLastFrameOfType(null, ACTION_USAGE);
            if (propUsage != null) {
                if (type.isProps() && !actionCompleted) {
                    quickLog("Completing actionUsage");
                    actionCompleted = true;

                    boolean res = false;
                    if (!res) res = completeActionInContextOfFormActionObject(propUsage);
                    if (!res) res = completeActionInContextOfMappedPropertiesList(propUsage);
                    if (!res) res = completeActionInNoContext(propUsage);
                    if (!res) res = completeActionInModifyParamOrFormContext(propUsage);
                    quickLog("Completed actionUsage");
                }

                return true;
            }
            return false;
        }

        private boolean completePropertyElseActionUsage() {
            Frame propUsage = getLastFrameOfType(null, PROPERTY_ELSE_ACTION_USAGE);
            if (propUsage != null) {
                if (type.isProps() && !propertyElseActionCompleted) {
                    quickLog("Completing propertyElseActionUsage");
                    propertyElseActionCompleted = true;

                    boolean res = false;
                    if (!res) res = completePropertyElseActionInContextOfFormPropertyObject(propUsage);
                    if (!res) res = completePropertyElseActionInContextOfMappedPropertiesList(propUsage);
                    if (!res) res = completePropertyElseActionInNoContext(propUsage);
                    quickLog("Completed propertyElseActionUsage");
                }

                return true;
            }
            return false;
        }

        private boolean completeFormElseNoParamsActionUsage() {
            Frame propUsage = getLastFrameOfType(null, FORM_ELSE_NO_PARAMS_ACTION_USAGE);
            if (propUsage != null) {
                if (type.isProps() && !formElseNoParamsActionCompleted) {
                    quickLog("Completing formElseNoParamsActionUsage");
                    formElseNoParamsActionCompleted = true;

                    String namespaceName = extractNamespace();

                    addLookupElements(
                            getVariantsFromIndices(namespaceName, file, asList(FormIndex.getInstance()), FORM_PRIORITY, getRequireScope(), getLocalScope())
                    );

                    Collection<LSFActionDeclaration> globalDeclarations = getDeclarationsFromScope(false);
                    addDeclarationsToLookup(new ArrayList<>(), null, namespaceName, globalDeclarations);

                    quickLog("Completed formElseNoParamsActionUsage");
                }

                return true;
            }
            return false;
        }

        private boolean completeDesignProperties() {
            Frame frame = getLastFrameOfType(null, DESIGN_STATEMENT);
            if (frame != null) {
                for (String designProperty : DESIGN_PROPERTIES) {
                    addLookupElement(createLookupElement(designProperty, DESIGN_PRIORITY, true, null));
                }
                return true;
            }
            return false;
        }

        private boolean completePropertyInImportUsage(Frame propUsage) {
            Frame importPropertyUsage = getLastFrameOfType(propUsage, IMPORT_PROPERTY_USAGE);
            if (importPropertyUsage != null) {
                LSFImportPropertyUsage psiImportPropertyUsage = getPsiOfTypeForFrame(importPropertyUsage, LSFImportPropertyUsage.class);
                if(psiImportPropertyUsage != null) {
                    completeProperties(
                            psiImportPropertyUsage.resolveParamClasses(),
                            MUST_USE_ALL
                    );
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
        private boolean completeActionInNoContext(Frame propUsage) {
            Frame frame = getLastFrameOfType(propUsage, NO_CONTEXT_ACTION_USAGE);
            if (frame != null) {
                completeActions(Collections.EMPTY_LIST, MAY_USE_ANY, true);
                return true;
            }
            return false;
        }
        private boolean completePropertyElseActionInNoContext(Frame propUsage) {
            Frame frame = getLastFrameOfType(propUsage, NO_CONTEXT_ACTION_OR_PROPERTY_USAGE);
            if (frame != null) {
                completePropertyElseActions(Collections.EMPTY_LIST, MAY_USE_ANY, true);
                return true;
            }
            return false;
        }

        private boolean completeActionInModifyParamOrFormContext(Frame propUsage) {
            PsiElement psi = getLastPsiOfType(false, ModifyParamContext.class, LSFFormStatement.class, LSFDesignStatement.class);
            if (psi != null) {
                completeActions(getContextClasses(psi, getOriginalFrameOffset(propUsage), getLocalScope(), false), MAY_USE_ANY);
                return true;
            }
            return false;
        }

        private boolean completeActionInContextOfFormActionObject(Frame propUsage) {
            Frame formMappedProperty = getLastFrameOfType(propUsage, FORM_ACTION_OBJECT);
            return formMappedProperty != null && completeActionInFormContext(formMappedProperty);
        }

        private boolean completePropertyElseActionInContextOfFormPropertyObject(Frame propUsage) {
            Frame formMappedProperty = getLastFrameOfType(propUsage, FORM_PROPERTY_DRAW_OBJECT);
            return formMappedProperty != null && completePropertyElseActionInFormContext(formMappedProperty);
        }

        private boolean completeActionInContextOfMappedPropertiesList(Frame propUsage) {
            Frame mappedPropertiesList = getLastFrameOfType(propUsage, FORM_MAPPED_NAME_PROPERTIES_LIST);
            if (mappedPropertiesList != null) {
                LSFFormMappedNamePropertiesList psiMappedPropertiesList = getPsiOfTypeForFrame(mappedPropertiesList, LSFFormMappedNamePropertiesList.class);
                if (psiMappedPropertiesList != null) {
                    completeActions(
                            psiMappedPropertiesList.resolveParamClasses(),
                            MUST_USE_ALL
                    );
                }
                return true;
            }
            return false;
        }
        private boolean completePropertyElseActionInContextOfMappedPropertiesList(Frame propUsage) {
            Frame mappedPropertiesList = getLastFrameOfType(propUsage, FORM_MAPPED_NAME_PROPERTIES_LIST);
            if (mappedPropertiesList != null) {
                LSFFormMappedNamePropertiesList psiMappedPropertiesList = getPsiOfTypeForFrame(mappedPropertiesList, LSFFormMappedNamePropertiesList.class);
                if (psiMappedPropertiesList != null) {
                    completePropertyElseActions(
                            psiMappedPropertiesList.resolveParamClasses(),
                            MUST_USE_ALL
                    );
                }
                return true;
            }
            return false;
        }

        // should be together because there can be modify param context and form context simultaneously (in form operator)
        private boolean completePropertyInModifyParamOrFormContext(Frame propUsage) {
            PsiElement psi = getLastPsiOfType(true, ModifyParamContext.class, LSFFormStatement.class, LSFDesignStatement.class);
            if (psi != null) {
                completeProperties(getContextClasses(psi, getOriginalFrameOffset(propUsage), getLocalScope(), false), MAY_USE_ANY);
                return true;
            }
            return false;
        }

        // should be together because there can be modify param context and form context simultaneously (in form operator)
        private boolean completeParameterInModifyParamOrFormContext(Frame paramDeclare) {
            PsiElement psi = getLastPsiOfType(true, ModifyParamContext.class, LSFFormStatement.class, LSFDesignStatement.class);
            if (psi != null) {
                LSFExprParamDeclaration currentParamDeclaration = getPsiOfTypeForFrame(paramDeclare, LSFExprParamDeclaration.class);
                
                Set<? extends LSFExprParamDeclaration> declarations = LSFPsiUtils.getContextParams(psi, getOriginalFrameOffset(paramDeclare), getLocalScope(), false);
                for (LSFExprParamDeclaration paramDeclaration : declarations) {
                    if (paramDeclaration != currentParamDeclaration) {
                        addLookupElement(createLookupElement(paramDeclaration, PARAM_PRIORITY));
                    }
                }
                return true;
            }
            return false;
        }

        private boolean completeActionInFormContext(Frame propUsage) {
            Frame formStatement = getLastFrameOfType(propUsage, FORM_STATEMENT);
            if (formStatement != null) {
                completeActionInContextOfFormStatement(formStatement);
                return true;
            }
            return false;
        }
        private boolean completePropertyElseActionInFormContext(Frame propUsage) {
            Frame formStatement = getLastFrameOfType(propUsage, FORM_STATEMENT);
            if (formStatement != null) {
                completePropertyElseActionInContextOfFormStatement(formStatement);
                return true;
            }
            return false;
        }

        private void completeActionInContextOfFormStatement(Frame startFrom) {
            LSFFormStatement psiFormStatement = getFormStatementPSI(startFrom);
            if (psiFormStatement != null) {
                completeActions(getContextClasses(psiFormStatement, getLocalScope(), true), MUST_USE_ANY);
            }
        }

        private void completePropertyElseActionInContextOfFormStatement(Frame startFrom) {
            LSFFormStatement psiFormStatement = getFormStatementPSI(startFrom);
            if (psiFormStatement != null) {
                completePropertyElseActions(getContextClasses(psiFormStatement, getLocalScope(), true), MUST_USE_ANY);
            }
        }

        private LSFFormStatement getFormStatementPSI(Frame childFrame) {
            Frame formStatement = getLastFrameOfType(childFrame, FORM_STATEMENT);
            if (formStatement != null) {
                return getPsiOfTypeForFrame(formStatement, LSFFormStatement.class);
            }
            return null;
        }

        public <G extends LSFFullNameDeclaration> List<G> getDeclarationsFromScope(boolean property) {
            return CompletionUtils.getDeclarationsFromScope(project, getRequireScope(), getLocalScope(), property ? PropIndex.getInstance() : ActionIndex.getInstance());
        }

        private void completeProperties(List<LSFClassSet> contextClasses, ClassUsagePolicy classUsagePolicy) {
            completeProperties(contextClasses, classUsagePolicy, false);
        }
        private void completeActions(List<LSFClassSet> contextClasses, ClassUsagePolicy classUsagePolicy) {
            completeActions(contextClasses, classUsagePolicy, false);
        }
        private void completePropertyElseActions(List<LSFClassSet> contextClasses, ClassUsagePolicy classUsagePolicy) {
            completePropertyElseActions(contextClasses, classUsagePolicy, false);
        }
        
        private void completeProperties(List<LSFClassSet> contextClasses, ClassUsagePolicy classUsagePolicy, boolean forceAll) {
            quickLog("After getContextClasses..");

            String namespaceName = extractNamespace();

            boolean isLight = type.isLight();
            boolean isHeavy = type.isHeavy();

            for (LSFClassSet classSet : contextClasses) {
                if (classSet != null) {
                    LSFValueClass valueClass = classSet.getCommonClass();
                    if (valueClass != null) {
                        addDeclarationsToLookup(contextClasses, classUsagePolicy, namespaceName, LSFPsiUtils.getPropertiesApplicableToClass(valueClass, project, getRequireScope(), getLocalScope(), isLight, isHeavy));
                    }
                }
            }

            quickLog("After getDeclarationsFromScope..");
            
            final Collection<LSFLocalPropDeclaration> localDeclarations = new ArrayList<>();
            // search local properties
            PsiElement lastElement = getLastPsiOfType(PsiElement.class);
            if (lastElement != null) {
                PsiTreeUtil.treeWalkUp(new BaseScopeProcessor() {
                    @Override
                    public boolean execute(@NotNull PsiElement element, @NotNull ResolveState state) {
                        if (element instanceof LSFLocalPropDeclaration) {
                            localDeclarations.add((LSFLocalPropDeclaration) element);
                        }
                        return true;
                    }
                }, lastElement, null, new ResolveState());
            }

            addDeclarationsToLookup(contextClasses, classUsagePolicy, null, localDeclarations);

            quickLog("After LOCAL searching..");

            if ((!isBasicCompletion || forceAll) && !isLight) {
                //search any other declarations
                Collection<LSFPropertyStatement> globalDeclarations = getDeclarationsFromScope(true);
                addDeclarationsToLookup(contextClasses, classUsagePolicy, namespaceName, globalDeclarations);
            }
        }

        private void completeActions(List<LSFClassSet> contextClasses, ClassUsagePolicy classUsagePolicy, boolean forceAll) {
            quickLog("After getContextClasses..");

            String namespaceName = extractNamespace();

            boolean isLight = type.isLight();
            boolean isHeavy = type.isHeavy();

            for (LSFClassSet classSet : contextClasses) {
                if (classSet != null) {
                    LSFValueClass valueClass = classSet.getCommonClass();
                    if (valueClass != null) {
                        addDeclarationsToLookup(contextClasses, classUsagePolicy, namespaceName, LSFPsiUtils.getActionsApplicableToClass(valueClass, project, getRequireScope(), getLocalScope(), isLight, isHeavy));
                    }
                }
            }

            quickLog("After getDeclarationsFromScope..");

            if ((!isBasicCompletion || forceAll) && !isLight) {
                //search any other declarations
                Collection<LSFActionStatement> globalDeclarations = getDeclarationsFromScope(false);
                addDeclarationsToLookup(contextClasses, classUsagePolicy, namespaceName, globalDeclarations);
            }
        }

        private void completePropertyElseActions(List<LSFClassSet> contextClasses, ClassUsagePolicy classUsagePolicy, boolean forceAll) {
            quickLog("After getContextClasses..");

            String namespaceName = extractNamespace();

            boolean isLight = type.isLight();
            boolean isHeavy = type.isHeavy();

            for (LSFClassSet classSet : contextClasses) {
                if (classSet != null) {
                    LSFValueClass valueClass = classSet.getCommonClass();
                    if (valueClass != null) {
                        addDeclarationsToLookup(contextClasses, classUsagePolicy, namespaceName, LSFPsiUtils.getActionsApplicableToClass(valueClass, project, getRequireScope(), getLocalScope(), isLight, isHeavy));
                        addDeclarationsToLookup(contextClasses, classUsagePolicy, namespaceName, LSFPsiUtils.getPropertiesApplicableToClass(valueClass, project, getRequireScope(), getLocalScope(), isLight, isHeavy));
                    }
                }
            }

            quickLog("After getDeclarationsFromScope..");

            if ((!isBasicCompletion || forceAll) && !isLight) {
                //search any other declarations
                Collection<LSFInterfacePropStatement> globalDeclarations = new ArrayList<>(); 
                globalDeclarations.addAll(getDeclarationsFromScope(true));
                globalDeclarations.addAll(getDeclarationsFromScope(false));
                addDeclarationsToLookup(contextClasses, classUsagePolicy, namespaceName, globalDeclarations);
            }
        }

        private <G extends LSFInterfacePropStatement> void addDeclarationsToLookup(List<LSFClassSet> contextClasses, ClassUsagePolicy classUsagePolicy, String namespace, Collection<G> declarations) {

            for (G declaration : declarations) {
//                assert namespace == null || declaration instanceof LSFGlobalPropDeclaration; // неправильный

                if (namespace != null && !(declaration instanceof LSFActionOrGlobalPropDeclaration && namespace.equals(((LSFActionOrGlobalPropDeclaration) declaration).getNamespaceName()))) {
                    continue;
                }

                double priority = declaration.isAction() ? ACTION_PRIORITY : PROPERTY_PRIORITY;

                if (contextClasses.isEmpty() && classUsagePolicy != MAY_USE_ANY) { // оптимизация для noParamsUsage (formElseNoParamsContextUsage например), при isNoParams все достается из stub'а без парсинга
                    if(!declaration.isNoParams())
                        priority = -1;
                } else {
                    List<LSFClassSet> declClasses = declaration.resolveParamClasses();
                    if (declClasses != null) {
                        if (classUsagePolicy == MUST_USE_ALL) {
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
                                if (priority != -1)
                                    priority += declClasses.size();
                            }
                        } else {
                            for (LSFClassSet declClass : declClasses) {
                                if (declClass != null) {
                                    boolean foundInContext = false;
                                    for (LSFClassSet contextClass : contextClasses) {
                                        if (declClass.containsAll(contextClass, true)) {
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
                }
                if (priority > 0) {
                    String name = declaration.getName();
                    if (name != null)
                        addLookupElement(
                                createLookupElement(
                                        name, declaration.getLookupObject(),
                                        declaration.getParamPresentableText(),
                                        declaration.isAction() ? "a" : "p", declaration.getValuePresentableText(), declaration.getLSFFile().getName(),
                                        declaration.getIcon(),
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
            return getLastPsiOfType(false, psiClass);
        }
        @Nullable
        private <T extends PsiElement> T getLastPsiOfType(boolean groupPropertyCheck, Class... psiClasses) {
            try {
                T result = null;
                for (int i = 0; i < framesCount; ++i) {
                    Frame currFrame = frames.get(i);
                    IElementType elementType = currFrame.type;
                    if (i == 0 || (elementType != null && !(elementType instanceof LSFTokenType))) {
                        PsiElement psiElement = i == 0 ? file : getPsiOfTypeForFrame(currFrame, getPsiElementClassByType(elementType));
                        if (psiElement == null) {
                            // it's tricky here, first of all, there are two rules where one is prefix of another : GROUP as and expr, and GROUP BY as expression unfriendly pd
                            // so GROUP BY is parsed first but there can be no BY in the end so PSI will be of GROUP as and expr branch, so thats the case we check below
                            // and in theory we continue proceeding frames since further there is no difference in groupPropertyDefinition and groupExprPropertyDefinition
                            if (groupPropertyCheck) {
                                if (elementType == EXPRESSION_UNFRIENDLY_PD && i + 1 < framesCount && frames.get(i+1).type == GROUP_PROPERTY_DEFINITION) {
                                    psiElement = getPsiOfTypeForFrame(currFrame, getPsiElementClassByType(PROPERTY_EXPRESSION));
                                    if(psiElement == null)
                                        break;
                                    i++;
                                    psiElement = getPsiOfTypeForFrame(currFrame, getPsiElementClassByType(GROUP_EXPR_PROPERTY_DEFINITION));
                                    if(psiElement == null)
                                        break;
                                }
                            }
                        }
                        for(Class<T> psiClass : psiClasses) {
                            if (psiClass.isInstance(psiElement)) {
                                result = (T) psiElement;
                            }
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
